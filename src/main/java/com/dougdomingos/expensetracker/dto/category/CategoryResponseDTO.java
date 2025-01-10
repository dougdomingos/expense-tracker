package com.dougdomingos.expensetracker.dto.category;

import java.util.Set;

import com.dougdomingos.expensetracker.entities.transaction.Transaction;
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

    @JsonProperty("transactionType")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @JsonProperty("transactions")
    private Set<Transaction> transactions;
}
