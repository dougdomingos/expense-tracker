package com.dougdomingos.expensetracker.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dougdomingos.expensetracker.dto.category.CategoryResponseDTO;
import com.dougdomingos.expensetracker.dto.category.CreateCategoryDTO;
import com.dougdomingos.expensetracker.dto.category.EditCategoryDTO;
import com.dougdomingos.expensetracker.services.category.CategoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @RequestBody @Valid CreateCategoryDTO categoryDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.createCategory(categoryDTO));
    }

    @GetMapping("/{idCategory}")
    public ResponseEntity<CategoryResponseDTO> getCategory(
            @PathVariable Long idCategory) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.getCategory(idCategory));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> listCategories() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.listCategories());
    }

    @PutMapping("/{idCategory}")
    public ResponseEntity<Object> editCategory(
            @PathVariable Long idCategory,
            @RequestBody @Valid EditCategoryDTO categoryDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.editCategory(idCategory, categoryDTO));
    }

    @DeleteMapping("/{idCategory}")
    public ResponseEntity<Void> removeTransaction(@PathVariable Long idCategory) {
        categoryService.removeCategory(idCategory);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
