package com.dougdomingos.expensetracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dougdomingos.expensetracker.entities.categories.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
