package com.dougdomingos.expensetracker.services.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;
import com.dougdomingos.expensetracker.entities.categories.Category;
import com.dougdomingos.expensetracker.entities.transaction.Transaction;
import com.dougdomingos.expensetracker.exceptions.category.CategoryTypeMismatchException;
import com.dougdomingos.expensetracker.repositories.CategoryRepository;
import com.dougdomingos.expensetracker.utils.EntityAccessUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryTransactionsServiceImpl implements CategoryTransactionsService {

    private final CategoryRepository categoryRepository;

    private final EntityAccessUtils entityAccessUtils;

    private final ModelMapper mapper;

    @Override
    public CategoryResponseDTO addTransactionToCategory(Long categoryId, Long transactionId) {
        Category category = entityAccessUtils.fetchUserCategory(categoryId);
        Transaction transactionToAdd = entityAccessUtils.fetchUserTransaction(transactionId);

        if (!category.matchesTypeOfCategory(transactionToAdd)) {
            throw new CategoryTypeMismatchException();
        }
        
        category.addTransaction(transactionToAdd);
        categoryRepository.save(category);
        
        return mapper.map(category, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO removeTransactionFromCategory(Long categoryId, Long transactionId) {
        Category category = entityAccessUtils.fetchUserCategory(categoryId);
        Transaction transactionToRemove = entityAccessUtils.fetchUserTransaction(transactionId);
        
        category.removeTransaction(transactionToRemove);
        categoryRepository.save(category);

        return mapper.map(category, CategoryResponseDTO.class);
    }
}
