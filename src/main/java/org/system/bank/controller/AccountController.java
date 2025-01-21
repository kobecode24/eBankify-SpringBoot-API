package org.system.bank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.enums.AccountStatus;
import org.system.bank.service.AccountService;
import org.system.bank.security.expression.AccountSecurityExpression;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
@PreAuthorize("isAuthenticated()")
public class AccountController {

    private final AccountService accountService;
    private final AccountSecurityExpression accountSecurity;

    @Operation(summary = "Create new account", description = "Creates a new bank account for a user")
    @ApiResponse(responseCode = "200", description = "Account created successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PreAuthorize("@accountSecurity.canCreateAccount(#request.userId)")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreationRequest request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @Operation(summary = "Get account details", description = "Retrieves account details by ID")
    @PreAuthorize("@accountSecurity.canAccessAccount(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @Operation(summary = "Update account", description = "Updates account details")
    @PreAuthorize("@accountSecurity.canModifyAccount(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountCreationRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    @Operation(summary = "Delete account", description = "Deletes an account from the system")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all accounts", description = "Retrieves all accounts in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @Operation(summary = "Get user accounts", description = "Retrieves all accounts for a specific user")
    @PreAuthorize("@accountSecurity.canAccessUserAccounts(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @Operation(summary = "Get accounts by status", description = "Retrieves accounts filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountResponse>> getAccountsByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(accountService.getAccountsByStatus(status));
    }

    @Operation(summary = "Update account status", description = "Updates the status of an account")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateAccountStatus(id, status));
    }

    @Operation(summary = "Get total balance", description = "Retrieves total balance for a user's accounts")
    @PreAuthorize("@accountSecurity.canAccessUserAccounts(#userId)")
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<Double> getTotalBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getTotalBalance(userId));
    }

    @Operation(summary = "Get accounts by minimum balance", description = "Retrieves accounts with balance above specified minimum")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/min-balance")
    public ResponseEntity<List<AccountResponse>> getAccountsWithMinBalance(@RequestParam Double minBalance) {
        return ResponseEntity.ok(accountService.getAccountsWithMinBalance(minBalance));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AccountResponse>> searchAccounts(
            @RequestParam String query) {
        return ResponseEntity.ok(accountService.searchAccounts(query));
    }
}
