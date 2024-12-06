package org.system.bank.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.system.bank.entity.Loan;
import org.system.bank.service.LoanService;

// org/system/bank/security/expression/LoanSecurityExpression.java
@Component("loanSecurity")
@RequiredArgsConstructor
public class LoanSecurityExpression extends SecurityExpressionRoot {

    private final LoanService loanService;

    public boolean canAccessLoan(Long loanId) {
        Loan loan = loanService.getLoanEntity(loanId);
        return isAdmin() ||
                isEmployee() ||
                loan.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canApproveLoan() {
        return isAdmin() || isEmployee();
    }

    public boolean canApplyForLoan() {
        return true; // All authenticated users can apply
    }
}
