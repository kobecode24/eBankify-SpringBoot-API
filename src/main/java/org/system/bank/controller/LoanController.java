package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin(origins = {"http://localhost:63342", "http://localhost:8080"})
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> applyForLoan(@Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.createLoan(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponse> getLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> updateLoan(
            @PathVariable Long id,
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity.ok(loanService.updateLoan(id, request));
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanResponse>> getLoansByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.getLoansByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanResponse>> getLoansByStatus(@PathVariable LoanStatus status) {
        return ResponseEntity.ok(loanService.getLoansByStatus(status));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponse>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.getOverdueLoans());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponse> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanResponse> rejectLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.rejectLoan(id));
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<LoanResponse> processLoanPayment(
            @PathVariable Long id,
            @RequestParam Double amount) {
        return ResponseEntity.ok(loanService.processLoanPayment(id, amount));
    }

    @GetMapping("/{id}/monthly-payment")
    public ResponseEntity<Double> calculateMonthlyPayment(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.calculateMonthlyPayment(id));
    }

    @GetMapping("/user/{userId}/total-debt")
    public ResponseEntity<Double> calculateTotalDebt(@PathVariable Long userId) {
        return ResponseEntity.ok(loanService.calculateTotalDebt(userId));
    }

    @GetMapping("/user/{userId}/eligibility")
    public ResponseEntity<Boolean> checkLoanEligibility(
            @PathVariable Long userId,
            @RequestParam Double amount) {
        return ResponseEntity.ok(loanService.isEligibleForLoan(userId, amount));
    }
}
