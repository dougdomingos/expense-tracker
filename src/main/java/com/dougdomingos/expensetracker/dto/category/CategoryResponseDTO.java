package com.dougdomingos.expensetracker.dto.category;

import java.util.List;

import com.dougdomingos.expensetracker.dto.transaction.TransactionResponseDTO;
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
public class CategoryResponseDTO {

    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("categoryType")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType categoryType;

    @JsonProperty("totalAmount")
    @NotNull
    private Double totalAmount;

    @JsonProperty("transactions")
    private List<TransactionResponseDTO> transactions;
}
