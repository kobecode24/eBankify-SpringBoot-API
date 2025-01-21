package org.system.bank.service;

import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.entity.Account;
import org.system.bank.enums.AccountStatus;

import java.util.List;

public interface AccountService {
    AccountResponse createAccount(AccountCreationRequest request);
    AccountResponse getAccountById(Long id);
    AccountResponse updateAccount(Long id, AccountCreationRequest request);
    void deleteAccount(Long id);
    List<AccountResponse> getAllAccounts();
    List<AccountResponse> getAccountsByUser(Long userId);
    List<AccountResponse> getAccountsByStatus(AccountStatus status);
    AccountResponse updateAccountStatus(Long accountId, AccountStatus status);
    Double getTotalBalance(Long userId);
    boolean hasActiveAccount(Long userId);
    List<AccountResponse> getAccountsWithMinBalance(Double minBalance);
    Account getAccountEntity(Long accountId);
    Account saveAccount(Account account);
    List<AccountResponse> searchAccounts(String query);

}