package com.dougdomingos.expensetracker.services.category;

import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;

public interface CategoryTransactionsService {

    CategoryResponseDTO addTransactionToCategory(Long categoryId, Long transactionId);

    CategoryResponseDTO removeTransactionFromCategory(Long categoryId, Long transactionId);
}
