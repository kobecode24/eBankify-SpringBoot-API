package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.system.bank.document.*;
import org.system.bank.service.ElasticsearchIndexService;
import org.system.bank.service.IndexingService;
import org.system.bank.service.search.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Validated
public class SearchController {
    private final TransactionSearchService transactionSearchService;
    private final UserSearchService userSearchService;
    private final AccountSearchService accountSearchService;
    private final LoanSearchService loanSearchService;
    private final InvoiceSearchService invoiceSearchService;
    private final ElasticsearchIndexService elasticsearchIndexService;
    private final IndexingService indexingService;


    // Transaction Search Endpoints
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDocument>> searchTransactions(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(transactionSearchService.searchTransactions(query,
                PageRequest.of(page, size)));
    }

    @GetMapping("/transactions/amount-range")
    public ResponseEntity<List<TransactionDocument>> findTransactionsByAmountRange(
            @RequestParam(defaultValue = "0.0") Double minAmount,
            @RequestParam(defaultValue = "999999999.99") Double maxAmount) {
        return ResponseEntity.ok(transactionSearchService.findByAmountRange(minAmount, maxAmount));
    }

    // User Search Endpoints
    @GetMapping("/users")
    public ResponseEntity<List<UserDocument>> searchUsers(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(userSearchService.searchUsers(query,
                PageRequest.of(page, size)));
    }

    @GetMapping("/users/eligible-borrowers")
    public ResponseEntity<List<UserDocument>> findEligibleBorrowers(
            @RequestParam(defaultValue = "650") Integer minCreditScore,
            @RequestParam(defaultValue = "3000.0") Double minMonthlyIncome,
            @RequestParam(defaultValue = "18") Integer minAge) {
        return ResponseEntity.ok(userSearchService.findEligibleBorrowers(
                minCreditScore, minMonthlyIncome, minAge));
    }

    // Loan Search Endpoints
    @GetMapping("/loans")
    public ResponseEntity<List<LoanDocument>> searchLoans(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(loanSearchService.searchLoans(query,
                PageRequest.of(page, size)));
    }

    @GetMapping("/loans/overdue")
    public ResponseEntity<List<LoanDocument>> findOverdueLoans() {
        return ResponseEntity.ok(loanSearchService.findOverdueLoans());
    }

    // Account Search Endpoints
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDocument>> searchAccounts(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(accountSearchService.searchAccounts(query,
                PageRequest.of(page, size)));
    }

    @GetMapping("/accounts/balance-range")
    public ResponseEntity<List<AccountDocument>> findAccountsByBalanceRange(
            @RequestParam(defaultValue = "0.0") Double minBalance,
            @RequestParam(defaultValue = "999999999.99") Double maxBalance) {
        return ResponseEntity.ok(accountSearchService.findByBalanceRange(minBalance, maxBalance));
    }

    // Invoice Search Endpoints
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceDocument>> searchInvoices(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(invoiceSearchService.searchInvoices(query,
                PageRequest.of(page, size)));
    }

    @GetMapping("/invoices/overdue")
    public ResponseEntity<List<InvoiceDocument>> findOverdueInvoices() {
        return ResponseEntity.ok(invoiceSearchService.findOverdueInvoices());
    }

    // Reindexing Endpoints
    @PostMapping("/reindex/all")
    public ResponseEntity<String> reindexAll() {
        try {
            indexingService.reindexAll();
            return ResponseEntity.ok("Successfully reindexed all documents");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error during reindexing: " + e.getMessage());
        }
    }

    @PostMapping("/reindex/transactions")
    public ResponseEntity<String> reindexTransactions() {
        try {
            indexingService.reindexTransactions();
            return ResponseEntity.ok("Successfully reindexed transactions");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reindexing transactions: " + e.getMessage());
        }
    }

    @PostMapping("/reindex/users")
    public ResponseEntity<String> reindexUsers() {
        try {
            indexingService.reindexUsers();
            return ResponseEntity.ok("Successfully reindexed users");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reindexing users: " + e.getMessage());
        }
    }

    @PostMapping("/reindex/accounts")
    public ResponseEntity<String> reindexAccounts() {
        try {
            indexingService.reindexAccounts();
            return ResponseEntity.ok("Successfully reindexed accounts");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reindexing accounts: " + e.getMessage());
        }
    }

    @PostMapping("/reindex/loans")
    public ResponseEntity<String> reindexLoans() {
        try {
            indexingService.reindexLoans();
            return ResponseEntity.ok("Successfully reindexed loans");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reindexing loans: " + e.getMessage());
        }
    }

    @PostMapping("/reindex/invoices")
    public ResponseEntity<String> reindexInvoices() {
        try {
            indexingService.reindexInvoices();
            return ResponseEntity.ok("Successfully reindexed invoices");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error reindexing invoices: " + e.getMessage());
        }
    }
}