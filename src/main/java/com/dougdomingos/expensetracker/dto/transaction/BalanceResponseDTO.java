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
public class BalanceResponseDTO {

    @JsonProperty("currentMonth")
    @NotBlank
    private String currentMonth;

    @JsonProperty("balance")
    @NotNull
    private Double balance;
}
