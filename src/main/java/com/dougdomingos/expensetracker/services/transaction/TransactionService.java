package com.dougdomingos.expensetracker.services.transaction;

import java.util.List;

import com.dougdomingos.expensetracker.dto.transaction.CreateTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.TransactionResponseDTO;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;

public interface TransactionService {

    TransactionResponseDTO createTransaction(CreateTransactionDTO transactionDTO);

    TransactionResponseDTO getTransaction(Long id);

    List<TransactionResponseDTO> listTransactions(TransactionType transactionType);

    TransactionResponseDTO editTransaction(Long id, Object transactionDTO);

    void removeTransaction(Long id);
}
