package com.dougdomingos.expensetracker.exceptions.user;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class PasswordInvalidException extends ExpenseTrackerException {
    public PasswordInvalidException() {
        super("Provided password is invalid");
    }
}
