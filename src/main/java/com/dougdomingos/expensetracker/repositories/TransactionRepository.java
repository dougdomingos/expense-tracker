package com.dougdomingos.expensetracker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByOwner(User owner);

    List<Transaction> findByOwnerAndTransactionType(User owner, TransactionType type);
}
