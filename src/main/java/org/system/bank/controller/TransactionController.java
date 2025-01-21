package org.system.bank.controller;
import org.springframework.data.domain.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.system.bank.config.SecurityUser;
import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.CartTransactionDTO;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.enums.OtpPurpose;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;
import org.system.bank.service.TransactionService;
import org.system.bank.otp.RequiresOtp;
import org.springframework.data.domain.PageRequest;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "APIs for managing banking transactions")
@PreAuthorize("isAuthenticated()")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Create new transaction", description = "Initiates a new transaction between accounts")
    @ApiResponse(responseCode = "200", description = "Transaction created successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient permissions or funds")
    @PreAuthorize("@transactionSecurity.canCreateTransaction(#request.sourceAccountId)")
    @PostMapping
    //@RequiresOtp(purpose = OtpPurpose.HIGH_VALUE_TRANSACTION)
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @Operation(summary = "Get transaction details", description = "Retrieves details of a specific transaction")
    @PreAuthorize("@transactionSecurity.canAccessTransaction(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionStatus status) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<TransactionResponse> transactions = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Get account transactions", description = "Retrieves all transactions for a specific account")
    @PreAuthorize("@transactionSecurity.canAccessAccountTransactions(#accountId)")
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    @Operation(summary = "Get transactions by type", description = "Retrieves transactions filtered by type")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByType(@PathVariable TransactionType type) {
        return ResponseEntity.ok(transactionService.getTransactionsByType(type));
    }

    @Operation(summary = "Get transactions by status", description = "Retrieves transactions filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByStatus(@PathVariable TransactionStatus status) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status));
    }

    @Operation(summary = "Get transaction history", description = "Retrieves transaction history for an account within a date range")
    @PreAuthorize("@transactionSecurity.canAccessAccountTransactions(#accountId)")
    @GetMapping("/history")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(transactionService.getAccountTransactionHistory(accountId, startDate, endDate));
    }

    @Operation(summary = "Get daily transaction total", description = "Calculates total transaction amount for an account on a specific date")
    @PreAuthorize("@transactionSecurity.canAccessAccountTransactions(#accountId)")
    @GetMapping("/daily-total")
    public ResponseEntity<Double> getDailyTransactions(
            @RequestParam Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(transactionService.calculateDailyTransactions(accountId, date));
    }

    @GetMapping("/pending")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CartTransactionDTO>> getPendingTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser user = (SecurityUser) authentication.getPrincipal();
        return ResponseEntity.ok(transactionService.transformToPendingTransactions(
                transactionService.getPendingTransactionsByUser(user.getUser())
        ));
    }
}
