package com.dougdomingos.expensetracker.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Record DTO for registering new users into the application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewUserDTO {

    @JsonProperty("username")
    @NotBlank(message = "Username is required")
    private String username;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    private String password;
}
