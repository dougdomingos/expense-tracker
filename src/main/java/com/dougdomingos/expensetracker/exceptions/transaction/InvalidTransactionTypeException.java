package com.dougdomingos.expensetracker.exceptions.transaction;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class InvalidTransactionTypeException extends ExpenseTrackerException {
    public InvalidTransactionTypeException() {
        super("Specified transaction type does not exist");
    }
}
