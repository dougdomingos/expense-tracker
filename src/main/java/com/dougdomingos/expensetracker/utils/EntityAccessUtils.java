package com.dougdomingos.expensetracker.utils;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.dougdomingos.expensetracker.auth.AuthUtils;
import com.dougdomingos.expensetracker.entities.categories.Category;
import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.category.CategoryNotFoundException;
import com.dougdomingos.expensetracker.exceptions.transaction.TransactionNotFoundException;
import com.dougdomingos.expensetracker.repositories.CategoryRepository;
import com.dougdomingos.expensetracker.repositories.TransactionRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityAccessUtils {

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private final CategoryRepository categoryRepository;

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
    public Transaction fetchUserTransaction(Long idTransaction)
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

    /**
     * Given a category ID, returns the category object.
     * 
     * @param idCategory The ID of the category to be fetched
     * @throws AccessDeniedException     Thrown if the requested category does
     *                                   not belong to the user
     * @throws CategoryNotFoundException Thrown if the requested category is
     *                                   not found in the database
     * @return The category object
     */
    public Category fetchUserCategory(Long idCategory)
            throws AccessDeniedException, CategoryNotFoundException {

        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        Category category = categoryRepository
                .findById(idCategory)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Current user does not own this category");
        }

        return category;
    }
}
