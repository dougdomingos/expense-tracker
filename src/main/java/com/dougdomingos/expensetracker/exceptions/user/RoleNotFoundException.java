package com.dougdomingos.expensetracker.exceptions.user;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class RoleNotFoundException extends ExpenseTrackerException {
    public RoleNotFoundException() {
        super("Provided role does not exist");
    }
}
