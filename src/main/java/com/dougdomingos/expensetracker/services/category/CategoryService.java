package com.dougdomingos.expensetracker.services.category;

import java.util.List;

import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;
import com.dougdomingos.expensetracker.dto.category.CreateCategoryDTO;
import com.dougdomingos.expensetracker.dto.category.EditCategoryDTO;

public interface CategoryService {

    CategoryResponseDTO createCategory(CreateCategoryDTO categoryDTO);

    CategoryResponseDTO getCategory(Long id);

    List<CategoryResponseDTO> listCategories();

    CategoryResponseDTO editCategory(Long id, EditCategoryDTO categoryDTO);

    void removeCategory(Long id);
}
