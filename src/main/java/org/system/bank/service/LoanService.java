package org.system.bank.service;

import org.system.bank.dto.request.LoanApplicationRequest;
import org.system.bank.dto.response.LoanResponse;
import org.system.bank.entity.Loan;
import org.system.bank.enums.LoanStatus;

import java.util.List;

public interface LoanService {
    LoanResponse createLoan(LoanApplicationRequest request);
    LoanResponse getLoanById(Long id);
    LoanResponse updateLoan(Long id, LoanApplicationRequest request);
    List<LoanResponse> getAllLoans();
    List<LoanResponse> getLoansByUser(Long userId);
    List<LoanResponse> getLoansByStatus(LoanStatus status);
    Double calculateTotalDebt(Long userId);
    Double calculateMonthlyPayment(Long loanId);
    boolean isEligibleForLoan(Long userId, Double amount);
    LoanResponse processLoanPayment(Long loanId, Double amount);
    List<LoanResponse> getOverdueLoans();
    boolean hasActiveLoan(Long userId);
    Long getDefaultedLoansCount(Long userId);
    LoanResponse approveLoan(Long loanId);
    LoanResponse rejectLoan(Long loanId);
    Double calculateInterestRate(Long userId, Double amount);
    Loan getLoanEntity(Long loanId);
}
