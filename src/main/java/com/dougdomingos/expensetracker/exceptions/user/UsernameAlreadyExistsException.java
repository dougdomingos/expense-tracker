package com.dougdomingos.expensetracker.exceptions.user;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class UsernameAlreadyExistsException extends ExpenseTrackerException {
    public UsernameAlreadyExistsException() {
        super("Provided username is already registered");
    }
}
