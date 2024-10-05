package com.dougdomingos.expensetracker.services.transaction;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.auth.AuthUtils;
import com.dougdomingos.expensetracker.dto.transaction.CreateTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.TransactionResponseDTO;
import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.transaction.TransactionNotFoundException;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private final ModelMapper mapper;

    @Override
    public TransactionResponseDTO createTransaction(CreateTransactionDTO transactionDTO) {
        User transactionOwner = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        Transaction newTransaction = mapper.map(transactionDTO, Transaction.class);
        newTransaction.setOwner(transactionOwner);

        transactionRepository.save(newTransaction);
        return mapper.map(newTransaction, TransactionResponseDTO.class);
    }

    @Override
    public TransactionResponseDTO editTransaction(Long id, Object transactionDTO) {
        Transaction transaction = fetchUserTransaction(id);

        mapper.map(transactionDTO, transaction);
        transactionRepository.save(transaction);

        return mapper.map(transaction, TransactionResponseDTO.class);
    }

    @Override
    public TransactionResponseDTO getTransaction(Long id) {
        Transaction transaction = fetchUserTransaction(id);
        return mapper.map(transaction, TransactionResponseDTO.class);
    }

    @Override
    public List<TransactionResponseDTO> listTransactions(TransactionType transactionType) {
        List<Transaction> transactions;
        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        if (transactionType == null) {
            transactions = transactionRepository
                    .findByOwner(currentUser);
        } else {
            transactions = transactionRepository
                    .findByOwnerAndTransactionType(currentUser, transactionType);
        }

        return transactions.stream()
                .map((transaction) -> mapper.map(transaction, TransactionResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void removeTransaction(Long id) {
        Transaction transaction = fetchUserTransaction(id);
        transactionRepository.delete(transaction);
    }

    /**
     * Given a transaction ID, returns the transaction object.
     * 
     * @param idTransaction The ID of the transaction to be fetched
     * @throws AccessDeniedException        Thrown if the requested transaction does
     *                                      not belong to the user
     * @throws TransactionNotFoundException Thrown if the requested transaction is
     *                                      not found in the database
     * @return The transaction object
     */
    private Transaction fetchUserTransaction(Long idTransaction)
            throws AccessDeniedException, TransactionNotFoundException {

        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        Transaction transaction = transactionRepository
                .findById(idTransaction)
                .orElseThrow(TransactionNotFoundException::new);

        if (!transaction.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Current user does not own this transaction");
        }

        return transaction;
    }

}
