package com.dougdomingos.expensetracker.services.category;

import java.util.List;

public interface CategoryService {

    Object createCategory(Object categoryDTO);

    Object getCategory(Long id);

    List<Object> listCategories();

    Object editCategory(Long id, Object categoryDTO);

    void removeCategory(Long id);
}
