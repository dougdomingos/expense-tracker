package com.dougdomingos.expensetracker.exceptions;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationErrorType {

    @JsonProperty("message")
    private String message;

    @JsonProperty("errors")
    private List<String> errors;

    public ApplicationErrorType(ExpenseTrackerException exception) {
        this.message = exception.getMessage(); 
        this.errors = new ArrayList<>();
    }
}
