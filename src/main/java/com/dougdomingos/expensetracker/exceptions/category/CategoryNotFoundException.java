package com.dougdomingos.expensetracker.exceptions.category;

import com.dougdomingos.expensetracker.exceptions.ExpenseTrackerException;

public class CategoryNotFoundException extends ExpenseTrackerException {
    
    public CategoryNotFoundException() {
        super("Specified category not found");
    }
}
