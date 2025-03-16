package com.dougdomingos.expensetracker.controllers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.dougdomingos.expensetracker.auth.TokenGenerator;
import com.dougdomingos.expensetracker.dto.transaction.BalanceResponseDTO;
import com.dougdomingos.expensetracker.dto.transaction.CreateTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.EditTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.TransactionResponseDTO;
import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.ApplicationErrorType;
import com.dougdomingos.expensetracker.exceptions.user.UserNotFoundException;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;
import com.dougdomingos.expensetracker.services.transaction.RecurrentTransactionService;
import com.dougdomingos.expensetracker.testutils.APITestClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("Integration tests for transaction features")
public class TransactionControllerTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    final APITestClient apiClient = new APITestClient("/transactions");

    @Autowired
    MockMvc driver;

    @Autowired
    CommandLineRunner dataInitializer;

    @Autowired
    TokenGenerator tokenGenerator;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    RecurrentTransactionService recurrentTransactionService;

    User testUser;

    @BeforeEach
    void setup() throws Exception {
        dataInitializer.run();
        apiClient.setDriver(driver);
        objectMapper
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        testUser = userRepository
                .findByUsername("admin")
                .orElseThrow(UserNotFoundException::new);

        apiClient.setAuthToken(tokenGenerator.generateToken(testUser, 120L));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Nested
    @DisplayName("Validations for creating transactions")
    class CreateTransactionValidationTests {

        private CreateTransactionDTO transactionDTO;

        @BeforeEach
        void setup() {
            transactionDTO = CreateTransactionDTO.builder()
                    .transactionType(TransactionType.INCOME)
                    .title("Test transaction")
                    .description("Transaction description")
                    .amount(100D)
                    .build();
        }

        @Test
        @DisplayName("Rejects creating transactions with null type")
        void whenCreateTransaction_withNullType_expectToFail() throws Exception {
            transactionDTO.setTransactionType(null);

            String responseJSON = apiClient.makePostRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Transaction type is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating transactions with null title")
        void whenCreateTransaction_withNullTitle_expectToFail() throws Exception {
            transactionDTO.setTitle(null);

            String responseJSON = apiClient.makePostRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Title is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating transactions with blank title")
        void whenCreateTransaction_withBlankTitle_expectToFail() throws Exception {
            transactionDTO.setTitle("");

            String responseJSON = apiClient.makePostRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Title is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating transactions with null amount")
        void whenCreateTransaction_withNullAmount_expectToFail() throws Exception {
            transactionDTO.setAmount(null);

            String responseJSON = apiClient.makePostRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Amount is required", result.getErrors().get(0)));
        }
    }

    @Nested
    @DisplayName("Validations for editing transaction")
    class EditTransactionValidationTests {

        private String requestRoute;

        private EditTransactionDTO transactionDTO;

        @BeforeEach
        void setup() {
            transactionDTO = EditTransactionDTO.builder()
                    .title("Test transaction")
                    .description("Transaction description")
                    .amount(0D)
                    .build();

            requestRoute = "/" + createTestTransaction(TransactionType.INCOME, 0).getTransactionId();
            apiClient.setRoute(requestRoute);
        }

        @Test
        @DisplayName("Rejects editing inexistent transaction")
        void whenEditTransaction_withInexistentTransaction_expectToFail() throws Exception {
            apiClient.setRoute("/" + 99999);

            String responseJSON = apiClient.makePutRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Specified transaction not found", result.getMessage()));
        }

        @Test
        @DisplayName("Rejects editing transactions with null title")
        void whenEditTransaction_withNullTitle_expectToFail() throws Exception {
            transactionDTO.setTitle(null);

            String responseJSON = apiClient.makePutRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Title is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects editing transactions with blank title")
        void whenEditTransaction_withBlankTitle_expectToFail() throws Exception {
            transactionDTO.setTitle("");

            String responseJSON = apiClient.makePutRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Title is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects editing transactions with null amount")
        void whenEditTransaction_withNullAmount_expectToFail() throws Exception {
            transactionDTO.setAmount(null);

            String responseJSON = apiClient.makePutRequest(transactionDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Amount is required", result.getErrors().get(0)));
        }
    }

    @Nested
    @DisplayName("Validations for listing transactions")
    class ListTransactionsValidationTests {

        private MultiValueMap<String, String> requestParams;

        @BeforeEach
        void setup() {
            requestParams = new LinkedMultiValueMap<>();
            createTestTransaction(TransactionType.INCOME, 0);
            createTestTransaction(TransactionType.EXPENSE, 0);
        }

        @Test
        @DisplayName("Returns the income transactions of a user")
        void whenListTransactions_withIncomeType_expectToListIncomesOnly() throws Exception {
            requestParams.add("type", TransactionType.INCOME.toString());
            apiClient.setParams(requestParams);

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            List<TransactionResponseDTO> result = objectMapper.readValue(responseJSON, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(1, countTransactionsByType(TransactionType.INCOME, result)),
                    () -> assertEquals(0, countTransactionsByType(TransactionType.EXPENSE, result)));
        }

        @Test
        @DisplayName("Returns the expense transactions of a user")
        void whenListTransactions_withExpenseType_expectToListExpensesOnly() throws Exception {
            requestParams.add("type", TransactionType.EXPENSE.toString());
            apiClient.setParams(requestParams);

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            List<TransactionResponseDTO> result = objectMapper.readValue(responseJSON, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(0, countTransactionsByType(TransactionType.INCOME, result)),
                    () -> assertEquals(1, countTransactionsByType(TransactionType.EXPENSE, result)));
        }

        @Test
        @DisplayName("Returns all the transactions of a user")
        void whenListTransactions_withoutFilter_expectToListAllTransactions() throws Exception {
            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            List<TransactionResponseDTO> result = objectMapper.readValue(responseJSON, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(1, countTransactionsByType(TransactionType.INCOME, result)),
                    () -> assertEquals(1, countTransactionsByType(TransactionType.EXPENSE, result)));
        }

        @Test
        @DisplayName("Rejects listing transactions with inexistent type")
        void whenListTransactions_withInexistentType_expectToFail() throws Exception {
            requestParams.add("type", "inexistent");
            apiClient.setParams(requestParams);

            String responseJSON = apiClient.makeGetRequest(null, status().isBadRequest());

            ApplicationErrorType result = objectMapper.readValue(
                    responseJSON,
                    ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Specified transaction type does not exist", result.getMessage()));
        }
    }

    @Nested
    @DisplayName("Balance validation tests")
    class BalanceValidationTests {

        private String currentMonth;

        @BeforeEach
        void setup() {
            apiClient.setRoute("/balance");

            currentMonth = Calendar
                    .getInstance()
                    .getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        }

        @Test
        @DisplayName("Calculating balance only considers transactions of current month")
        void whenCalculatingBalance_withOlderTransactions_expectToCountOnlyCurrentMonth() throws Exception {
            transactionRepository.save(Transaction.builder()
                    .transactionType(TransactionType.INCOME)
                    .title("Old transaction")
                    .amount(250D)
                    .createdAt(LocalDateTime.of(2000, 1, 1, 12, 0, 0))
                    .build());

            createTestTransaction(TransactionType.INCOME, 250);

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            BalanceResponseDTO result = objectMapper
                    .readValue(responseJSON, BalanceResponseDTO.BalanceResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(currentMonth, result.getCurrentMonth()),
                    () -> assertEquals(250, result.getBalance()));
        }

        @Test
        @DisplayName("Calculating balance with no transactions returns zero")
        void whenCalculatingBalance_withNoTransactions_expectToReturnZero() throws Exception {
            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            BalanceResponseDTO result = objectMapper
                    .readValue(responseJSON, BalanceResponseDTO.BalanceResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(currentMonth, result.getCurrentMonth()),
                    () -> assertEquals(0, result.getBalance()));
        }

        @Test
        @DisplayName("Calculating balance may return a positive value")
        void whenCalculatingBalance_withPositiveValue_expectToPass() throws Exception {
            double expectedValue = 500;
            createTestTransaction(TransactionType.INCOME, expectedValue);

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            BalanceResponseDTO result = objectMapper
                    .readValue(responseJSON, BalanceResponseDTO.BalanceResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(currentMonth, result.getCurrentMonth()),
                    () -> assertEquals(expectedValue, result.getBalance()));
        }

        @Test
        @DisplayName("Calculating balance may return a negative value")
        void whenCalculatingBalance_withNegativeValue_expectToPass() throws Exception {
            double expectedValue = -500;
            createTestTransaction(TransactionType.EXPENSE, expectedValue);

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            BalanceResponseDTO result = objectMapper
                    .readValue(responseJSON, BalanceResponseDTO.BalanceResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(currentMonth, result.getCurrentMonth()),
                    () -> assertEquals(expectedValue, result.getBalance()));
        }
    }

    @Nested
    @DisplayName("Transaction recurrence tests")
    class TransactionRecurrenceTests {

        @BeforeEach
        void setup() {
            transactionRepository.save(Transaction.builder()
                    .transactionType(TransactionType.INCOME)
                    .isRecurrent(true)
                    .title("Recurrent transaction")
                    .amount(500D)
                    .owner(testUser)
                    .build());
        }

        @Test
        @DisplayName("Number of recurrent transactions does not change after update")
        void whenUpdatingRecurrentTransactions_expectConstantNumOfRecurrentTransactions() {
            int numTransactionsBeforeUpdate = transactionRepository.findByIsRecurrentTrue().size();
            recurrentTransactionService.updateRecurrentTransactionsOnDB();
            int numTransactionsAfterUpdate = transactionRepository.findByIsRecurrentTrue().size();

            assertEquals(numTransactionsBeforeUpdate, numTransactionsAfterUpdate);
        }

        @Test
        @DisplayName("Old records of a recurrent transaction are preserved")
        void whenUpdatingRecurrentTransactions_expectToKeepOldRecord() {
            recurrentTransactionService.updateRecurrentTransactionsOnDB();
            
            List<Transaction> transactions = transactionRepository.findByOwner(testUser);
            Transaction oldRecord = transactions.get(0);
            Transaction newRecord = transactions.get(1);

            assertAll(
                    () -> assertEquals(oldRecord.getTransactionType(), newRecord.getTransactionType()),
                    () -> assertEquals(oldRecord.getAmount(), newRecord.getAmount()),
                    () -> assertFalse(oldRecord.isRecurrent()),
                    () -> assertTrue(newRecord.isRecurrent()),
                    () -> assertTrue(newRecord.getCreatedAt().isAfter(oldRecord.getCreatedAt())));
        }
    }

    @Nested
    @DisplayName("Tests for CRUD features")
    class CRUDFeaturesTests {

        @Test
        @DisplayName("Accept creating income with valid data")
        void whenCreateIncome_withValidData_expectToPass() throws Exception {
            CreateTransactionDTO newTransaction = CreateTransactionDTO.builder()
                    .transactionType(TransactionType.INCOME)
                    .title("Income transaction")
                    .description("Income transaction test")
                    .amount(200D)
                    .build();

            String responseJSON = apiClient.makePostRequest(newTransaction, status().isCreated());

            TransactionResponseDTO result = objectMapper
                    .readValue(responseJSON, TransactionResponseDTO.TransactionResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(newTransaction.getTransactionType(), result.getTransactionType()),
                    () -> assertEquals(newTransaction.getTitle(), result.getTitle()),
                    () -> assertEquals(newTransaction.getDescription(), result.getDescription()),
                    () -> assertEquals(newTransaction.getAmount(), result.getAmount()));
        }

        @Test
        @DisplayName("Accept creating expense with valid data")
        void whenCreateExpense_withValidData_expectToPass() throws Exception {
            CreateTransactionDTO newTransaction = CreateTransactionDTO.builder()
                    .transactionType(TransactionType.EXPENSE)
                    .title("Expense transaction")
                    .description("Expense transaction test")
                    .amount(200D)
                    .build();

            String responseJSON = apiClient.makePostRequest(newTransaction, status().isCreated());

            TransactionResponseDTO result = objectMapper
                    .readValue(responseJSON, TransactionResponseDTO.TransactionResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(newTransaction.getTransactionType(), result.getTransactionType()),
                    () -> assertEquals(newTransaction.getTitle(), result.getTitle()),
                    () -> assertEquals(newTransaction.getDescription(), result.getDescription()),
                    () -> assertEquals(-newTransaction.getAmount(), result.getAmount()));
        }

        @Test
        @DisplayName("Accept creating transaction without description")
        void whenCreateExpense_withoutDescription_expectToPass() throws Exception {
            CreateTransactionDTO newTransaction = CreateTransactionDTO.builder()
                    .transactionType(TransactionType.INCOME)
                    .title("Test transaction")
                    .amount(200D)
                    .build();

            String responseJSON = apiClient.makePostRequest(newTransaction, status().isCreated());

            TransactionResponseDTO result = objectMapper
                    .readValue(responseJSON, TransactionResponseDTO.TransactionResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(newTransaction.getTransactionType(), result.getTransactionType()),
                    () -> assertEquals(newTransaction.getTitle(), result.getTitle()),
                    () -> assertEquals(newTransaction.getDescription(), result.getDescription()),
                    () -> assertEquals(newTransaction.getAmount(), result.getAmount()));
        }

        @Test
        @DisplayName("Accept reading existent transaction")
        void whenReadingTransaction_withValidID_expectToPass() throws Exception {
            Transaction testTransaction = createTestTransaction(TransactionType.INCOME, 100);
            apiClient.setRoute("/" + testTransaction.getTransactionId());

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            TransactionResponseDTO result = objectMapper
                    .readValue(responseJSON, TransactionResponseDTO.TransactionResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(testTransaction.getTransactionType(), result.getTransactionType()),
                    () -> assertEquals(testTransaction.getTitle(), result.getTitle()),
                    () -> assertEquals(testTransaction.getDescription(), result.getDescription()),
                    () -> assertEquals(testTransaction.getAmount(), result.getAmount()));
        }

        @Test
        @DisplayName("Accept editing transaction with valid data")
        void whenEditTransaction_withValidData_expectToPass() throws Exception {
            Transaction testTransaction = createTestTransaction(TransactionType.INCOME, 100);

            EditTransactionDTO editedTransaction = EditTransactionDTO.builder()
                    .title("Edited transaction title")
                    .description("Edited transaction description")
                    .amount(0D)
                    .build();

            apiClient.setRoute("/" + testTransaction.getTransactionId());
            String responseJSON = apiClient.makePutRequest(editedTransaction, status().isOk());

            TransactionResponseDTO result = objectMapper
                    .readValue(responseJSON, TransactionResponseDTO.TransactionResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(editedTransaction.getTitle(), result.getTitle()),
                    () -> assertEquals(editedTransaction.getDescription(), result.getDescription()),
                    () -> assertEquals(editedTransaction.getAmount(), result.getAmount()));
        }

        @Test
        @DisplayName("Accept removing existent transaction")
        void whenRemovingExistentTransaction_expectToPass() throws Exception {
            Transaction testTransaction = createTestTransaction(TransactionType.INCOME, 100);

            apiClient.setRoute("/" + testTransaction.getTransactionId());
            String responseJSON = apiClient.makeDeleteRequest(null, status().isNoContent());

            assertAll(
                    () -> assertTrue(responseJSON.isBlank()),
                    () -> assertFalse(transactionRepository.findById(testTransaction.getTransactionId()).isPresent()));
        }
    }

    /**
     * Creates an instance of a transaction and saves it into the database.
     * 
     * @param type The type of the transaction
     * @return The transaction object
     */
    private Transaction createTestTransaction(TransactionType type, double value) {
        return transactionRepository.save(Transaction.builder()
                .transactionType(type)
                .title("Test transaction")
                .description("Transaction description")
                .amount(value)
                .createdAt(LocalDateTime.now())
                .owner(testUser)
                .build());
    }

    /**
     * Counts the number of transactions with a specified type.
     * 
     * @param type         The targeted type
     * @param transactions The list of transactions
     * @return The number of transactions with the specified type.
     */
    private long countTransactionsByType(TransactionType type, List<TransactionResponseDTO> transactions) {
        return transactions
                .stream()
                .filter(transaction -> transaction.getTransactionType().equals(type))
                .count();
    }
}
