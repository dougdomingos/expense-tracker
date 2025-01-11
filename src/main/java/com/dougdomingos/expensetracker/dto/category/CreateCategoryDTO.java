package com.dougdomingos.expensetracker.dto.category;

import com.dougdomingos.expensetracker.entities.transaction.TransactionType;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDTO {

    @JsonProperty("name")
    @NotBlank(message = "Category name is required")
    private String name;
    
    @JsonProperty("categoryType")
    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    private TransactionType categoryType;
    
}
