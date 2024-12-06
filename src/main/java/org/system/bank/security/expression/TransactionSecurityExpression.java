package org.system.bank.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.system.bank.entity.Account;
import org.system.bank.entity.Transaction;
import org.system.bank.service.AccountService;
import org.system.bank.service.TransactionService;

@Component("transactionSecurity")
@RequiredArgsConstructor
public class TransactionSecurityExpression extends SecurityExpressionRoot {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public boolean canAccessTransaction(Long transactionId) {
        Transaction transaction = transactionService.getTransactionEntity(transactionId);
        return isAdmin() ||
                isEmployee() ||
                canAccessAccount(transaction.getSourceAccount().getAccountId()) ||
                canAccessAccount(transaction.getDestinationAccount().getAccountId());
    }

    public boolean canCreateTransaction(Long sourceAccountId) {
        Account account = accountService.getAccountEntity(sourceAccountId);
        return isAdmin() || account.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    private boolean canAccessAccount(Long accountId) {
        Account account = accountService.getAccountEntity(accountId);
        return account.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canAccessAccountTransactions(Long accountId) {
        return isAdmin() ||
                isEmployee() ||
                canAccessAccount(accountId);
    }


}
