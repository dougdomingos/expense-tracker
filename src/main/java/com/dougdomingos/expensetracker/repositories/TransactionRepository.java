package com.dougdomingos.expensetracker.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Fetch all transactions of a specific user.
     * 
     * @param owner The user that owns the transactions
     * @return A list of transactions
     */
    List<Transaction> findByOwner(User owner);

    /**
     * Fetch transactions of a specific user, filtered by type.
     * 
     * @param owner The user that owns the transactions
     * @param type  The targeted transaction type
     * @return A list of transactions that match the given type
     */
    List<Transaction> findByOwnerAndTransactionType(User owner, TransactionType type);

    /**
     * Fetch all transactions created in a specified time interval.
     * 
     * @param start The base date of the interval
     * @param end   The limit date of the interval
     * @return A list of transactions that match the given interval
     */
    List<Transaction> findByOwnerAndCreatedAtBetween(User owner, LocalDateTime start, LocalDateTime end);

    /**
     * Featch all recurrent transactions.
     * 
     * @return A list of transactions that are recurrent
     */
    List<Transaction> findByIsRecurrentTrue();
}
