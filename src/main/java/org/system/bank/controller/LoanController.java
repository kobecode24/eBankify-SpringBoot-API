package org.system.bank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.system.bank.dto.request.LoanApplicationRequest;
import org.system.bank.dto.response.LoanResponse;
import org.system.bank.enums.LoanStatus;
import org.system.bank.service.LoanService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Management", description = "APIs for managing bank loans")
@PreAuthorize("isAuthenticated()")
public class LoanController {

    private final LoanService loanService;

    @Operation(summary = "Apply for loan", description = "Creates a new loan application")
    @ApiResponse(responseCode = "200", description = "Loan application created successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PreAuthorize("@loanSecurity.canApplyForLoan()")
    @PostMapping
    public ResponseEntity<LoanResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.createLoan(request));
    }

    @Operation(summary = "Get loan details", description = "Retrieves details of a specific loan")
    @PreAuthorize("@loanSecurity.canAccessLoan(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @Operation(summary = "Update loan", description = "Updates an existing loan application")
    @PreAuthorize("@loanSecurity.canAccessLoan(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> updateLoan(
            @PathVariable Long id,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.updateLoan(id, request));
    }

    @Operation(summary = "Get all loans", description = "Retrieves all loans in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @Operation(summary = "Get user loans", description = "Retrieves all loans for a specific user")
    @PreAuthorize("@userSecurity.canAccessUserData(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUser(userId));
    }

    @Operation(summary = "Get loans by status", description = "Retrieves loans filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanResponse>> getLoansByStatus(@PathVariable LoanStatus status) {
        return ResponseEntity.ok(loanService.getLoansByStatus(status));
    }

    @Operation(summary = "Get overdue loans", description = "Retrieves all overdue loans")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }

    @Operation(summary = "Approve loan", description = "Approves a pending loan application")
    @PreAuthorize("@loanSecurity.canApproveLoan()")
    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

    @Operation(summary = "Reject loan", description = "Rejects a pending loan application")
    @PreAuthorize("@loanSecurity.canApproveLoan()")
    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.rejectLoan(id));
    }

    @Operation(summary = "Process loan payment", description = "Processes a payment for an active loan")
    @PreAuthorize("@loanSecurity.canAccessLoan(#id)")
    @PostMapping("/{id}/payment")
    public ResponseEntity<LoanResponse> processLoanPayment(
            @PathVariable Long id,
            @RequestParam Double amount) {
        return ResponseEntity.ok(loanService.processLoanPayment(id, amount));
    }

    @Operation(summary = "Calculate monthly payment", description = "Calculates monthly payment for a loan")
    @PreAuthorize("@loanSecurity.canAccessLoan(#id)")
    @GetMapping("/{id}/monthly-payment")
    public ResponseEntity<Double> calculateMonthlyPayment(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.calculateMonthlyPayment(id));
    }

    @Operation(summary = "Calculate total debt", description = "Calculates total debt for a user")
    @PreAuthorize("@userSecurity.canAccessUserData(#userId)")
    @GetMapping("/user/{userId}/total-debt")
    public ResponseEntity<Double> calculateTotalDebt(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.calculateTotalDebt(userId));
    }

    @Operation(summary = "Check loan eligibility", description = "Checks if a user is eligible for a loan amount")
    @PreAuthorize("@userSecurity.canAccessUserData(#userId)")
    @GetMapping("/user/{userId}/eligibility")
    public ResponseEntity<Boolean> checkLoanEligibility(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        return ResponseEntity.ok(loanService.isEligibleForLoan(userId, amount));
    }
}
