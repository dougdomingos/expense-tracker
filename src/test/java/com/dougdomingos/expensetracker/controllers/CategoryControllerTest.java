package com.dougdomingos.expensetracker.controllers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.dougdomingos.expensetracker.auth.TokenGenerator;
import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;
import com.dougdomingos.expensetracker.dto.category.CreateCategoryDTO;
import com.dougdomingos.expensetracker.dto.category.EditCategoryDTO;
import com.dougdomingos.expensetracker.entities.categories.Category;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.ApplicationErrorType;
import com.dougdomingos.expensetracker.exceptions.user.UserNotFoundException;
import com.dougdomingos.expensetracker.repositories.CategoryRepository;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;
import com.dougdomingos.expensetracker.testutils.APITestClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for category features")
public class CategoryControllerTest {

    final ObjectMapper objectMapper = new ObjectMapper();

    final APITestClient apiClient = new APITestClient("/categories");

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
    CategoryRepository categoryRepository;

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
    @DisplayName("Validations for creating categories")
    class CreateCategoryValidationTests {

        private CreateCategoryDTO categoryDTO;

        @BeforeEach
        void setup() {
            categoryDTO = CreateCategoryDTO.builder()
                    .name("Test category")
                    .transactionType(TransactionType.INCOME)
                    .build();
        }

        @Test
        @DisplayName("Rejects creating categories with null name")
        void whenCreateCategory_withNullTitle_expectToFail() throws Exception {
            categoryDTO.setName(null);

            String responseJSON = apiClient.makePostRequest(categoryDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Category name is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating categories with blank name")
        void whenCreateCategory_withBlankTitle_expectToFail() throws Exception {
            categoryDTO.setName("");

            String responseJSON = apiClient.makePostRequest(categoryDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Category name is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating categories with null type")
        void whenCreateCategory_withNullType_expectToFail() throws Exception {
            categoryDTO.setTransactionType(null);

            String responseJSON = apiClient.makePostRequest(categoryDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Transaction type is required", result.getErrors().get(0)));
        }
    }

    @Nested
    @DisplayName("Validations for editing categories")
    class EditCategoryValidationTests {

        private EditCategoryDTO categoryDTO;

        @BeforeEach
        void setup() {
            categoryDTO = EditCategoryDTO.builder()
                    .name("Test edit")
                    .build();

            String requestRoute = "/" + createTestCategory(TransactionType.INCOME).getCategoryId();
            apiClient.setRoute(requestRoute);
        }

        @Test
        @DisplayName("Rejects editing inexistent categories")
        void whenEditCategory_withInexistentCategory_expectToFail() throws Exception {
            apiClient.setRoute("/" + 99999);

            String responseJSON = apiClient.makePutRequest(categoryDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertEquals("Specified category not found", result.getMessage());
        }

        @Test
        @DisplayName("Rejects editing transactions with null name")
        void whenEditCategory_withNullName_expectToFail() throws Exception {
            categoryDTO.setName(null);

            String responseJSON = apiClient.makePutRequest(categoryDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Category name is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects editing categories with blank name")
        void whenEditCategories_withBlankName_expectToFail() throws Exception {
            categoryDTO.setName(null);

            String responseJSON = apiClient.makePutRequest(categoryDTO, status().isBadRequest());

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Category name is required", result.getErrors().get(0)));
        }
    }

    @Nested
    @DisplayName("Validations for listing categories")
    class ListCategoryValidationTests {

        @Test
        @DisplayName("Rejects reading inexistent categories")
        void whenReadingCategory_withInexistentCategory_expectToFail() throws Exception {
            apiClient.setRoute("/" + 99999);
            String responseJSON = apiClient.makeGetRequest(null, status().isBadRequest());

            ApplicationErrorType result = objectMapper.readValue(responseJSON, ApplicationErrorType.class);

            assertEquals("Specified category not found", result.getMessage());
        }
    }

    @Nested
    @DisplayName("Validations for removing categories")
    class RemoveCategoryValidationTests {

        @Test
        @DisplayName("Rejects removing inexistent categories")
        void whenRemovingCategory_withInexistentCategory_expectToFail() throws Exception {
            apiClient.setRoute("/" + 99999);
            String responseJSON = apiClient.makeDeleteRequest(null, status().isBadRequest());

            ApplicationErrorType result = objectMapper.readValue(responseJSON, ApplicationErrorType.class);

            assertEquals("Specified category not found", result.getMessage());
        }
    }

    @Nested
    @DisplayName("Test for basic CRUD features")
    class CRUDFeaturesTests {

        @Test
        @DisplayName("Accepts creating categories with valid data")
        void whenCreateCategory_withValidData_expectToPass() throws Exception {
            CreateCategoryDTO newCategory = CreateCategoryDTO.builder()
                    .name("Test category")
                    .transactionType(TransactionType.INCOME)
                    .build();

            String responseJSON = apiClient.makePostRequest(newCategory, status().isCreated());

            CategoryResponseDTO result = objectMapper
                    .readValue(responseJSON, CategoryResponseDTO.CategoryResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(newCategory.getName(), result.getName()),
                    () -> assertEquals(newCategory.getTransactionType(), result.getTransactionType()));
        }

        @Test
        @DisplayName("Accepts editing categories with valid data")
        void whenEditCategory_withValidData_expectToPass() throws Exception {
            Category testCategory = createTestCategory(TransactionType.INCOME);

            EditCategoryDTO editedCategory = EditCategoryDTO.builder()
                    .name("Edited category")
                    .build();

            apiClient.setRoute("/" + testCategory.getCategoryId());
            String responseJSON = apiClient.makePutRequest(editedCategory, status().isOk());

            CategoryResponseDTO result = objectMapper
                    .readValue(responseJSON, CategoryResponseDTO.CategoryResponseDTOBuilder.class)
                    .build();

            assertEquals(editedCategory.getName(), result.getName());
        }

        @Test
        @DisplayName("Accepts reading existent category")
        void whenReadingCategory_withValidID_expectToPass() throws Exception {
            Category testCategory = createTestCategory(TransactionType.INCOME);

            apiClient.setRoute("/" + testCategory.getCategoryId());
            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            CategoryResponseDTO result = objectMapper
                    .readValue(responseJSON, CategoryResponseDTO.CategoryResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertEquals(testCategory.getName(), result.getName()),
                    () -> assertEquals(testCategory.getTransactionType(), result.getTransactionType()),
                    () -> assertNotNull(result.getTransactions()));
        }

        @Test
        @DisplayName("Accepts listing all categories of a user")
        void whenListingCategories_ofCurrentUser_expectToPass() throws Exception {
            Category category1 = createTestCategory(TransactionType.INCOME);
            Category category2 = createTestCategory(TransactionType.EXPENSE);

            String responseJSON = apiClient.makeGetRequest(null, status().isOk());

            List<CategoryResponseDTO> result = objectMapper.readValue(responseJSON, new TypeReference<>() {
            });

            assertAll(
                    () -> assertEquals(2, result.size()),
                    () -> assertEquals(category1.getTransactionType(), result.get(0).getTransactionType()),
                    () -> assertEquals(category2.getTransactionType(), result.get(1).getTransactionType()));
        }

        @Test
        @DisplayName("Accepts removing existent categories")
        void whenRemovingCategory_withValidID_expectToPass() throws Exception {
            Category testCategory = createTestCategory(TransactionType.INCOME);

            apiClient.setRoute("/" + testCategory.getCategoryId());
            String responseJSON = apiClient.makeDeleteRequest(null, status().isNoContent());

            assertAll(
                    () -> assertTrue(responseJSON.isEmpty()),
                    () -> assertFalse(categoryRepository.findById(testCategory.getCategoryId()).isPresent()));
        }
    }

    /**
     * Creates an instance of a category and saves it into the database.
     * 
     * @param type The type of the category
     * @return The category object
     */
    private Category createTestCategory(TransactionType type) {
        return categoryRepository.save(Category.builder()
                .name("Test category")
                .transactionType(type)
                .owner(testUser)
                .build());
    }
}
