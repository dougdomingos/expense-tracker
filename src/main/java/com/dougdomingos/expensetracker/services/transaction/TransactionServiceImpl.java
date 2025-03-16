package com.dougdomingos.expensetracker.services.transaction;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.auth.AuthUtils;
import com.dougdomingos.expensetracker.dto.transaction.CreateTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.TransactionResponseDTO;
import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.transaction.InvalidTransactionTypeException;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;
import com.dougdomingos.expensetracker.utils.EntityAccessUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private final EntityAccessUtils entityAccessUtils;

    private final ModelMapper mapper;

    @Override
    public TransactionResponseDTO createTransaction(CreateTransactionDTO transactionDTO) {

        User transactionOwner = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());
        Transaction newTransaction = mapper.map(transactionDTO, Transaction.class);

        Double absAmount = Math.abs(newTransaction.getAmount());
        if (newTransaction.getTransactionType().equals(TransactionType.EXPENSE)) {
            absAmount *= -1;
        }

        newTransaction.setAmount(absAmount);
        newTransaction.setOwner(transactionOwner);

        transactionRepository.save(newTransaction);
        return mapper.map(newTransaction, TransactionResponseDTO.class);
    }

    @Override
    public TransactionResponseDTO editTransaction(Long id, Object transactionDTO) {
        Transaction transaction = entityAccessUtils.fetchUserTransaction(id);

        mapper.map(transactionDTO, transaction);
        transactionRepository.save(transaction);

        return mapper.map(transaction, TransactionResponseDTO.class);
    }

    @Override
    public TransactionResponseDTO getTransaction(Long id) {
        Transaction transaction = entityAccessUtils.fetchUserTransaction(id);
        return mapper.map(transaction, TransactionResponseDTO.class);
    }

    @Override
    public List<TransactionResponseDTO> listTransactions(String type) {
        List<Transaction> transactions;
        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        if (type == null || type.isBlank()) {
            transactions = transactionRepository.findByOwner(currentUser);
        } else if (TransactionType.isTypeDefined(type)) {
            transactions = transactionRepository.findByOwnerAndTransactionType(
                    currentUser,
                    TransactionType.valueOf(type.toUpperCase()));
        } else {
            throw new InvalidTransactionTypeException();
        }

        return transactions.stream()
                .map((transaction) -> mapper.map(transaction, TransactionResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void removeTransaction(Long id) {
        Transaction transaction = entityAccessUtils.fetchUserTransaction(id);
        transactionRepository.delete(transaction);
    }

}
