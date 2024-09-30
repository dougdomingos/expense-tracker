package com.dougdomingos.expensetracker.exceptions;

public class ExpenseTrackerException extends RuntimeException {
    public ExpenseTrackerException() {
        super("Unexpected error has occured!");
    }

    public ExpenseTrackerException(String msg) {
        super(msg);
    }
}
