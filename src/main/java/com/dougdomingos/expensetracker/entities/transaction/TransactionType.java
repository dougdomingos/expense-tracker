package com.dougdomingos.expensetracker.entities.transaction;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public static boolean isTypeDefined(String type) {
        for (TransactionType definedType : TransactionType.values()) {
            if (definedType.name().equals(type.toUpperCase())) {
                return true;
            }
        }

        return false;
    }
}