package org.system.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.entity.Account;
import org.system.bank.entity.User;
import org.system.bank.enums.AccountStatus;
import org.system.bank.mapper.AccountMapper;
import org.system.bank.repository.jpa.AccountRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.AccountServiceImpl;
import org.system.bank.util.TestDataBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest extends BaseServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account testAccount;
    private User testUser;
    private AccountCreationRequest testRequest;
    private AccountResponse testResponse;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createTestUser();
        testAccount = TestDataBuilder.createTestAccount();
        testRequest = TestDataBuilder.createTestAccountCreationRequest();
        testResponse = AccountResponse.builder()
                .accountId(testAccount.getAccountId())
                .balance(testAccount.getBalance())
                .status(testAccount.getStatus())
                .userId(testUser.getUserId())
                .userName(testUser.getName())
                .build();
    }

    @Test
    void createAccount_ShouldReturnAccountResponse() {
        // Arrange
        when(userService.getUserEntity(anyLong())).thenReturn(testUser);
        when(accountMapper.toEntity(any(AccountCreationRequest.class))).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.toResponse(any(Account.class))).thenReturn(testResponse);

        // Act
        AccountResponse result = accountService.createAccount(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getBalance(), result.getBalance());
        assertEquals(testAccount.getStatus(), result.getStatus());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountExists() {
        // Arrange
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));
        when(accountMapper.toResponse(any(Account.class))).thenReturn(testResponse);

        // Act
        AccountResponse result = accountService.getAccountById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getBalance(), result.getBalance());
        verify(accountRepository).findById(1L);
    }

    @Test
    void updateAccountStatus_ShouldReturnUpdatedAccount() {
        // Arrange
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.toResponse(any(Account.class))).thenReturn(testResponse);

        // Act
        AccountResponse result = accountService.updateAccountStatus(1L, AccountStatus.BLOCKED);

        // Assert
        assertNotNull(result);
        verify(accountRepository).save(any(Account.class));
    }

    // Continuing AccountServiceTest

    @Test
    void getAccountsByUser_ShouldReturnUserAccounts() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount, testAccount);
        when(userService.getUserEntity(anyLong())).thenReturn(testUser);
        when(accountRepository.findByUser(any(User.class))).thenReturn(accounts);
        when(accountMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<AccountResponse> result = accountService.getAccountsByUser(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository).findByUser(any(User.class));
    }

    @Test
    void getAccountsByStatus_ShouldReturnAccountsWithSpecificStatus() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount, testAccount);
        when(accountRepository.findByStatus(any(AccountStatus.class))).thenReturn(accounts);
        when(accountMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<AccountResponse> result = accountService.getAccountsByStatus(AccountStatus.ACTIVE);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository).findByStatus(AccountStatus.ACTIVE);
    }

    @Test
    void getTotalBalance_ShouldReturnTotalBalance() {
        // Arrange
        when(userService.getUserEntity(anyLong())).thenReturn(testUser);
        when(accountRepository.getTotalBalance(any(User.class))).thenReturn(2000.0);

        // Act
        Double result = accountService.getTotalBalance(1L);

        // Assert
        assertEquals(2000.0, result);
        verify(accountRepository).getTotalBalance(any(User.class));
    }

    @Test
    void hasActiveAccount_ShouldReturnTrue_WhenUserHasActiveAccount() {
        // Arrange
        when(userService.getUserEntity(anyLong())).thenReturn(testUser);
        when(accountRepository.hasActiveAccount(any(User.class))).thenReturn(true);

        // Act
        boolean result = accountService.hasActiveAccount(1L);

        // Assert
        assertTrue(result);
        verify(accountRepository).hasActiveAccount(any(User.class));
    }

    @Test
    void getAccountsWithMinBalance_ShouldReturnQualifiedAccounts() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount, testAccount);
        when(accountRepository.findAccountsWithBalanceGreaterThan(anyDouble())).thenReturn(accounts);
        when(accountMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<AccountResponse> result = accountService.getAccountsWithMinBalance(1000.0);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository).findAccountsWithBalanceGreaterThan(1000.0);
    }
}