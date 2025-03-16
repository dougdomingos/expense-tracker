package com.dougdomingos.expensetracker.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dougdomingos.expensetracker.dto.transaction.BalanceResponseDTO;
import com.dougdomingos.expensetracker.dto.transaction.CreateTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.EditTransactionDTO;
import com.dougdomingos.expensetracker.dto.transaction.TransactionResponseDTO;
import com.dougdomingos.expensetracker.services.transaction.BalanceService;
import com.dougdomingos.expensetracker.services.transaction.TransactionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

    private final TransactionService transactionService;

    private final BalanceService balanceService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody @Valid CreateTransactionDTO transactionDTO) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.createTransaction(transactionDTO));
    }

    @GetMapping("/{idTransaction}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable Long idTransaction) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.getTransaction(idTransaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByType(
            @RequestParam(required = false) String type) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.listTransactions(type));
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponseDTO> getCurrentBalance() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(balanceService.getCurrentBalance());
    }

    @PutMapping("/{idTransaction}")
    public ResponseEntity<TransactionResponseDTO> editTransaction(
            @PathVariable Long idTransaction,
            @RequestBody @Valid EditTransactionDTO transactionDTO) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(transactionService.editTransaction(idTransaction, transactionDTO));
    }

    @DeleteMapping("/{idTransaction}")
    public ResponseEntity<Void> removeTransaction(@PathVariable Long idTransaction) {
        transactionService.removeTransaction(idTransaction);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
