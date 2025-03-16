package com.dougdomingos.expensetracker.services.transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecurrentTransactionServiceImpl implements RecurrentTransactionService {

    private final TransactionRepository transactionRepository;

    private final ModelMapper mapper;

    @Override
    @Scheduled(cron = "0 0 0 1 * *")
    public void updateRecurrentTransactionsOnDB() {
        List<Transaction> recurrentTransactions = transactionRepository
                .findByIsRecurrentTrue();

        for (Transaction transaction : recurrentTransactions) {
            Transaction newTransaction = new Transaction();
            mapper.map(transaction, newTransaction);

            transaction.setRecurrent(false);
            newTransaction.setTransactionId(null);
            newTransaction.setCreatedAt(LocalDateTime.now());

            transactionRepository.saveAll(List.of(transaction, newTransaction));
        }

    }
}
