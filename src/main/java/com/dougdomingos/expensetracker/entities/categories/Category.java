package com.dougdomingos.expensetracker.entities.categories;

import java.util.HashSet;
import java.util.Set;

import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.dougdomingos.expensetracker.entities.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType categoryType;

    @Builder.Default
    @Column(nullable = false)
    private Double totalAmount = 0.0;

    @Builder.Default
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId")
    private Set<Transaction> transactions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    /**
     * Add the specified transaction to this category, while incrementing its total
     * value. If the operation fails, the category value is not updated.
     * 
     * @param transaction The transaction to be inserted into this category.
     * @return A boolean value; true if the operation succeeds, false otherwise
     */
    public boolean addTransaction(Transaction transaction) {
        boolean wasTransactionAdded = transactions.add(transaction);
        if (wasTransactionAdded) {
            totalAmount += transaction.getAmount();
        }

        return wasTransactionAdded;
    }

    /**
     * Remove the specified transaction from this category, while decrementing its
     * total value. If the operation fails, the category value is not updated.
     * 
     * @param transaction The transaction to be removed from this category.
     * @return A boolean value; true if the operation succeeds, false otherwise
     */
    public boolean removeTransaction(Transaction transaction) {
        boolean wasTransactionRemoved = transactions.remove(transaction);
        if (wasTransactionRemoved) {
            totalAmount -= transaction.getAmount();
        }

        return wasTransactionRemoved;
    }

    /**
     * Verifies if the type of the specified transaction matches the type of this
     * category.
     * 
     * @param transaction The transaction to be analyzed.
     * @return A boolean value; true if the type of the transaction matches the type
     *         of this category, false otherwise
     */
    public boolean matchesTypeOfCategory(Transaction transaction) {
        return categoryType.equals(transaction.getTransactionType());
    }
}
