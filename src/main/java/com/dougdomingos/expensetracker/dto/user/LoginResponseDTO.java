package com.dougdomingos.expensetracker.dto.user;

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
public class LoginResponseDTO {

    @JsonProperty("accessToken")
    @NotBlank
    private String accessToken;

    @JsonProperty("expiresIn")
    @NotNull
    private Long expiresIn;
}