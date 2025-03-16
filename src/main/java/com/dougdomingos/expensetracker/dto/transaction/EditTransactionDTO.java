package com.dougdomingos.expensetracker.dto.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class EditTransactionDTO {

    @JsonProperty("isRecurrent")
    private boolean isRecurrent;

    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("amount")
    @NotNull(message = "Amount is required")
    private Double amount;
}
