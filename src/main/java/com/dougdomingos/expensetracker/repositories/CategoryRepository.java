package com.dougdomingos.expensetracker.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dougdomingos.expensetracker.entities.categories.Category;
import com.dougdomingos.expensetracker.entities.user.User;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Fetch all categories of a specific user.
     * 
     * @param owner The user that owns the categories
     * @return A list of categories
     */
    List<Category> findByOwner(User owner);
}
