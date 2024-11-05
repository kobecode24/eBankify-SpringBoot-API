package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;
import org.system.bank.service.TransactionService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(@PathVariable TransactionType type) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(type));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @RequestParam Long accountId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getAccountTransactionHistory(accountId, startDate, endDate));
    }

    @GetMapping("/daily-total")
    public ResponseEntity<Double> getDailyTransactions(
            @RequestParam Long accountId,
            @RequestParam LocalDateTime date) {
        return ResponseEntity.ok(transactionService.calculateDailyTransactions(accountId, date));
    }
}
