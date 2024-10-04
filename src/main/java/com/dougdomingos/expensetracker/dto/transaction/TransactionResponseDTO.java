package com.dougdomingos.expensetracker.dto.transaction;

import java.time.LocalDateTime;

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
public class TransactionResponseDTO {

    @JsonProperty("id")
    @NotNull
    private Long id;

    @JsonProperty("transactionType")
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @JsonProperty("title")
    @NotBlank
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("amount")
    @NotNull
    private Double amount;

    @JsonProperty("createdAt")
    @NotNull
    private LocalDateTime createdAt;
}
