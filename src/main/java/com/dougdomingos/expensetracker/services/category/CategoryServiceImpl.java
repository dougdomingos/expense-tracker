package com.dougdomingos.expensetracker.services.category;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.auth.AuthUtils;
import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;
import com.dougdomingos.expensetracker.dto.category.CreateCategoryDTO;
import com.dougdomingos.expensetracker.dto.category.EditCategoryDTO;
import com.dougdomingos.expensetracker.entities.categories.Category;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.repositories.CategoryRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;
import com.dougdomingos.expensetracker.utils.EntityAccessUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final EntityAccessUtils entityAccessUtils;

    private final ModelMapper mapper;

    @Override
    public CategoryResponseDTO createCategory(CreateCategoryDTO categoryDTO) {

        User categoryOwner = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());
        Category newCategory = mapper.map(categoryDTO, Category.class);

        newCategory.setOwner(categoryOwner);

        categoryRepository.save(newCategory);
        return mapper.map(newCategory, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO editCategory(Long id, EditCategoryDTO categoryDTO) {
        Category category = entityAccessUtils.fetchUserCategory(id);

        mapper.map(categoryDTO, category);
        categoryRepository.save(category);

        return mapper.map(category, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO getCategory(Long id) {
        Category category = entityAccessUtils.fetchUserCategory(id);
        return mapper.map(category, CategoryResponseDTO.class);
    }

    @Override
    public List<CategoryResponseDTO> listCategories() {
        List<Category> categories;
        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        categories = categoryRepository.findByOwner(currentUser);

        return categories.stream()
                .map((category) -> mapper.map(category, CategoryResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void removeCategory(Long id) {
        Category category = entityAccessUtils.fetchUserCategory(id);
        categoryRepository.delete(category);
    }

}
