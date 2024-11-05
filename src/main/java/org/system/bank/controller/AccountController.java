package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.enums.AccountStatus;
import org.system.bank.service.AccountService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountCreationRequest request) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountCreationRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountsByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountResponse>> getAccountsByStatus(@PathVariable AccountStatus status) {
        return ResponseEntity.ok(accountService.getAccountsByStatus(status));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponse> updateAccountStatus(
            @PathVariable Long id,
            @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateAccountStatus(id, status));
    }

    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<Double> getTotalBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getTotalBalance(userId));
    }

    @GetMapping("/min-balance")
    public ResponseEntity<List<AccountResponse>> getAccountsWithMinBalance(@RequestParam Double minBalance) {
        return ResponseEntity.ok(accountService.getAccountsWithMinBalance(minBalance));
    }
}
