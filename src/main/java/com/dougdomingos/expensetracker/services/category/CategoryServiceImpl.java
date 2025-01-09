package com.dougdomingos.expensetracker.services.category;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.auth.AuthUtils;
import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;
import com.dougdomingos.expensetracker.dto.category.CreateCategoryDTO;
import com.dougdomingos.expensetracker.dto.category.EditCategoryDTO;
import com.dougdomingos.expensetracker.entities.categories.Category;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.category.CategoryNotFoundException;
import com.dougdomingos.expensetracker.repositories.CategoryRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

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
        Category category = fetchUserCategory(id);

        mapper.map(categoryDTO, category);
        categoryRepository.save(category);

        return mapper.map(category, CategoryResponseDTO.class);
    }

    @Override
    public CategoryResponseDTO getCategory(Long id) {
        Category category = fetchUserCategory(id);
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
        Category category = fetchUserCategory(id);
        categoryRepository.delete(category);
    }

    /**
     * Given a category ID, returns the category object.
     * 
     * @param idCategory The ID of the category to be fetched
     * @throws AccessDeniedException     Thrown if the requested category does
     *                                   not belong to the user
     * @throws CategoryNotFoundException Thrown if the requested category is
     *                                   not found in the database
     * @return The category object
     */
    private Category fetchUserCategory(Long idCategory)
            throws AccessDeniedException, CategoryNotFoundException {

        User currentUser = userRepository.findByUserId(AuthUtils.getAuthenticatedUserID());

        Category category = categoryRepository
                .findById(idCategory)
                .orElseThrow(CategoryNotFoundException::new);

        if (!category.getOwner().equals(currentUser)) {
            throw new AccessDeniedException("Current user does not own this category");
        }

        return category;
    }

}
