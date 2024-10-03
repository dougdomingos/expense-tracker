package com.dougdomingos.expensetracker.exceptions.user;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class UserNotFoundException extends ExpenseTrackerException {
    public UserNotFoundException() {
        super("Specified user not found");
    }
}
