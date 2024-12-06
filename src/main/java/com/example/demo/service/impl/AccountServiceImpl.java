package com.example.demo.service.impl;


import com.example.demo.dto.request.AccountCreationRequest;
import com.example.demo.entities.Account;
import com.example.demo.entities.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserService;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Override
    public AccountResponse createAccount(AccountCreationRequest request) {
        User user = userService.getUserEntity(request.getUserId());

        Account account = Account.builder()
                .balance(request.getInitialDeposit())
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        Account savedAccount = accountRepository.save(account);
        return convertToResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountById(Long id) {
        Account account = getAccountEntity(id);
        return convertToResponse(account);
    }

    @Override
    public Account getAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public AccountResponse updateAccount(Long id, AccountCreationRequest request) {
        Account existingAccount = getAccountEntity(id);
        User user = userService.getUserEntity(request.getUserId());

        existingAccount.setBalance(request.getInitialDeposit());
        existingAccount.setUser(user);

        Account updatedAccount = accountRepository.save(existingAccount);
        return convertToResponse(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account", "id", id);
        }
        accountRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUser(Long userId) {
        User user = userService.getUserEntity(userId);
        return accountRepository.findByUser(user).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponse updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = getAccountEntity(accountId);
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);
        return convertToResponse(updatedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalBalance(Long userId) {
        User user = userService.getUserEntity(userId);
        return accountRepository.getTotalBalance(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveAccount(Long userId) {
        User user = userService.getUserEntity(userId);
        return accountRepository.hasActiveAccount(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsWithMinBalance(Double minBalance) {
        return accountRepository.findAccountsWithBalanceGreaterThan(minBalance).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private AccountResponse convertToResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .balance(account.getBalance())
                .status(account.getStatus())
                .userId(account.getUser().getUserId())
                .userName(account.getUser().getEmail())
                .build();
    }
}
