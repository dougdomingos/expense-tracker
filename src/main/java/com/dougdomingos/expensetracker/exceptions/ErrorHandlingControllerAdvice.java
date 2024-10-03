package com.dougdomingos.expensetracker.exceptions;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApplicationErrorType onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApplicationErrorType customErrorType = buildApplicationError(
                "Validation errors have occurred");

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            customErrorType.getErrors().add(fieldError.getDefaultMessage());
        }

        return customErrorType;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ApplicationErrorType onConstraintViolation(ConstraintViolationException e) {
        ApplicationErrorType customErrorType = buildApplicationError(
                "Validation errors have occurred");

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            customErrorType.getErrors().add(violation.getMessage());
        }

        return customErrorType;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExpenseTrackerException.class)
    public ApplicationErrorType onExpenseTrackerException(ExpenseTrackerException e) {
        return buildApplicationError(e.getMessage());
    }

    /**
     * Returns a custom application error.
     * 
     * @param message The error message
     * @return An {@code ApplicationErrorType} object
     */
    private ApplicationErrorType buildApplicationError(String message) {
        return ApplicationErrorType.builder()
                .errors(new ArrayList<>())
                .message(message)
                .build();
    }

}
