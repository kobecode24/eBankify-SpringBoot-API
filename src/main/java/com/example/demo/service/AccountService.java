package com.example.demo.service;


import com.example.demo.dto.request.AccountCreationRequest;
import com.example.demo.entities.Account;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.enums.AccountStatus;

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
}
