package org.system.bank.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.system.bank.entity.Account;
import org.system.bank.service.AccountService;

@Component("accountSecurity")
@RequiredArgsConstructor
public class AccountSecurityExpression extends SecurityExpressionRoot {

    private final AccountService accountService;

    public boolean canAccessAccount(Long accountId) {
        Account account = accountService.getAccountEntity(accountId);
        return isAdmin() ||
                isEmployee() ||
                account.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canModifyAccount(Long accountId) {
        Account account = accountService.getAccountEntity(accountId);
        return isAdmin() || account.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canUpdateStatus(Long accountId) {
        return isAdmin();
    }

    public boolean canCreateAccount(Long userId) {
        return isAdmin() || getCurrentUser().getUserId().equals(userId);
    }

    public boolean canAccessUserAccounts(Long userId) {
        return isAdmin() ||
                isEmployee() ||
                getCurrentUser().getUserId().equals(userId);
    }
}