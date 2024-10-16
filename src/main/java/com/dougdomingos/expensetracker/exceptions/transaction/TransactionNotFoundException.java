package com.dougdomingos.expensetracker.exceptions.transaction;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class TransactionNotFoundException extends ExpenseTrackerException {
    
    public TransactionNotFoundException() {
        super("Specified transaction not found");
    }
}
