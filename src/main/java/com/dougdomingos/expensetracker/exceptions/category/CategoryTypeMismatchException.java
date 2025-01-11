package com.dougdomingos.expensetracker.exceptions.category;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class CategoryTypeMismatchException extends ExpenseTrackerException {
    public CategoryTypeMismatchException() {
        super("Transaction type does not match category type");
    }
}