package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.entity.Account;
import org.system.bank.entity.User;
import org.system.bank.enums.AccountStatus;
import org.system.bank.mapper.AccountMapper;
import org.system.bank.repository.AccountRepository;
import org.system.bank.service.AccountService;
import org.system.bank.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserService userService;

    @Override
    public AccountResponse createAccount(AccountCreationRequest request) {
        User user = userService.getUserEntity(request.getUserId());

        Account account = accountMapper.toEntity(request);
        account.setUser(user);
        Account savedAccount = accountRepository.save(account);

        return accountMapper.toResponse(savedAccount);
    }

    @Override
    public AccountResponse getAccountById(Long id) {
        Account account = getAccountEntity(id);
        return accountMapper.toResponse(account);
    }

    @Override
    public Account getAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
    }

    @Override
    public Account saveAccount(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public AccountResponse updateAccount(Long id, AccountCreationRequest request) {
        Account existingAccount = getAccountEntity(id);
        User user = userService.getUserEntity(request.getUserId());

        Account accountToUpdate = accountMapper.toEntity(request);
        accountToUpdate.setAccountId(id);
        accountToUpdate.setUser(user);
        accountToUpdate.setStatus(existingAccount.getStatus());

        Account updatedAccount = accountRepository.save(accountToUpdate);
        return accountMapper.toResponse(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new EntityNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountMapper.toResponseList(accountRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByUser(Long userId) {
        User user = userService.getUserEntity(userId);
        return accountMapper.toResponseList(accountRepository.findByUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByStatus(AccountStatus status) {
        return accountMapper.toResponseList(accountRepository.findByStatus(status));
    }

    @Override
    public AccountResponse updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = getAccountEntity(accountId);
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);
        return accountMapper.toResponse(updatedAccount);
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
        return accountMapper.toResponseList(
                accountRepository.findAccountsWithBalanceGreaterThan(minBalance)
        );
    }
}