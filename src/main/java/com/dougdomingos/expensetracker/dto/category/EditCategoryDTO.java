package com.dougdomingos.expensetracker.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditCategoryDTO {
    
    @JsonProperty("name")
    @NotBlank(message = "Category name is required")
    private String name;
}
