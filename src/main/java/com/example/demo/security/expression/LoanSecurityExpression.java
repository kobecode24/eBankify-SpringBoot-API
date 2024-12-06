package com.example.demo.security.expression;


import com.example.demo.entities.Account;
import com.example.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("loanSecurity")
@RequiredArgsConstructor
public class LoanSecurityExpression extends SecurityExpressionRoot {

    private final AccountService accountService;

    public boolean canAccessLoan(Long loanId) {
        Account account = accountService.getAccountEntity(loanId);
        return isAdmin() ||
                isEmployee() ||
                account.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canApproveLoan() {
        return isAdmin() || isEmployee();
    }

    public boolean canApplyForLoan() {
        return true; // All authenticated users can apply
    }
}
