package org.system.bank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.system.bank.repository.base.BaseRepositoryTest;
import org.system.bank.entity.Account;
import org.system.bank.entity.User;
import org.system.bank.enums.AccountStatus;
import org.system.bank.enums.Role;
import org.system.bank.repository.jpa.AccountRepository;
import org.system.bank.repository.jpa.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword123")
                .age(30)
                .monthlyIncome(5000.0)
                .creditScore(750)
                .role(Role.USER)
                .build();
        testUser = userRepository.save(testUser);

        testAccount = Account.builder()
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
    }

    @Test
    void findByUser_ShouldReturnAccounts_WhenUserHasAccounts() {
        // Arrange
        Account savedAccount = accountRepository.save(testAccount);
        Account anotherAccount = Account.builder()
                .balance(2000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        accountRepository.save(anotherAccount);

        // Act
        List<Account> accounts = accountRepository.findByUser(testUser);

        // Assert
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().allMatch(account -> account.getUser().equals(testUser)));
    }

    @Test
    void findByStatus_ShouldReturnAccounts_WhenStatusMatches() {
        // Arrange
        accountRepository.save(testAccount);
        Account blockedAccount = Account.builder()
                .balance(2000.0)
                .status(AccountStatus.BLOCKED)
                .user(testUser)
                .build();
        accountRepository.save(blockedAccount);

        // Act
        List<Account> activeAccounts = accountRepository.findByStatus(AccountStatus.ACTIVE);
        List<Account> blockedAccounts = accountRepository.findByStatus(AccountStatus.BLOCKED);

        // Assert
        assertEquals(1, activeAccounts.size());
        assertEquals(1, blockedAccounts.size());
    }

    @Test
    void findAccountsWithBalanceGreaterThan_ShouldReturnQualifiedAccounts() {
        // Arrange
        accountRepository.save(testAccount);
        Account highBalanceAccount = Account.builder()
                .balance(5000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        accountRepository.save(highBalanceAccount);

        // Act
        List<Account> accounts = accountRepository.findAccountsWithBalanceGreaterThan(2000.0);

        // Assert
        assertEquals(1, accounts.size());
        assertTrue(accounts.stream().allMatch(account -> account.getBalance() > 2000.0));
    }

    @Test
    void hasActiveAccount_ShouldReturnTrue_WhenUserHasActiveAccount() {
        // Arrange
        accountRepository.save(testAccount);

        // Act
        boolean hasActive = accountRepository.hasActiveAccount(testUser);

        // Assert
        assertTrue(hasActive);
    }

    @Test
    void getTotalBalance_ShouldReturnCorrectSum_WhenUserHasMultipleAccounts() {
        // Arrange
        accountRepository.save(testAccount);
        Account secondAccount = Account.builder()
                .balance(2000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        accountRepository.save(secondAccount);

        // Act
        Double totalBalance = accountRepository.getTotalBalance(testUser);

        // Assert
        assertEquals(3000.0, totalBalance);
    }
}
