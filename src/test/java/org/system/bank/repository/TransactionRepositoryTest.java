package org.system.bank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.system.bank.repository.base.BaseRepositoryTest;
import org.system.bank.entity.Account;
import org.system.bank.entity.Transaction;
import org.system.bank.entity.User;
import org.system.bank.enums.AccountStatus;
import org.system.bank.enums.Role;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Account sourceAccount;
    private Account destinationAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
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

        // Create test accounts
        sourceAccount = Account.builder()
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        sourceAccount = accountRepository.save(sourceAccount);

        destinationAccount = Account.builder()
                .balance(2000.0)
                .status(AccountStatus.ACTIVE)
                .user(testUser)
                .build();
        destinationAccount = accountRepository.save(destinationAccount);

        // Create test transaction
        testTransaction = Transaction.builder()
                .type(TransactionType.STANDARD)
                .amount(100.0)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findBySourceAccount_ShouldReturnTransactions() {
        // Arrange
        Transaction savedTransaction = transactionRepository.save(testTransaction);

        // Act
        List<Transaction> transactions = transactionRepository.findBySourceAccount(sourceAccount);

        // Assert
        assertEquals(1, transactions.size());
        assertEquals(savedTransaction.getTransactionId(), transactions.get(0).getTransactionId());
    }

    @Test
    void findByDestinationAccount_ShouldReturnTransactions() {
        // Arrange
        Transaction savedTransaction = transactionRepository.save(testTransaction);

        // Act
        List<Transaction> transactions = transactionRepository.findByDestinationAccount(destinationAccount);

        // Assert
        assertEquals(1, transactions.size());
        assertEquals(savedTransaction.getTransactionId(), transactions.get(0).getTransactionId());
    }

    @Test
    void findByType_ShouldReturnTransactions() {
        // Arrange
        transactionRepository.save(testTransaction);

        // Act
        List<Transaction> standardTransactions = transactionRepository.findByType(TransactionType.STANDARD);
        List<Transaction> instantTransactions = transactionRepository.findByType(TransactionType.INSTANT);

        // Assert
        assertEquals(1, standardTransactions.size());
        assertTrue(instantTransactions.isEmpty());
    }

    @Test
    void findByStatus_ShouldReturnTransactions() {
        // Arrange
        transactionRepository.save(testTransaction);

        // Act
        List<Transaction> pendingTransactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        List<Transaction> completedTransactions = transactionRepository.findByStatus(TransactionStatus.COMPLETED);

        // Assert
        assertEquals(1, pendingTransactions.size());
        assertTrue(completedTransactions.isEmpty());
    }

    @Test
    void findByCreatedAtBetween_ShouldReturnTransactionsInTimeRange() {
        // Arrange
        Transaction savedTransaction = transactionRepository.save(testTransaction);
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        // Act
        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(start, end);

        // Assert
        assertEquals(1, transactions.size());
        assertEquals(savedTransaction.getTransactionId(), transactions.get(0).getTransactionId());
    }


    @Test
    void calculateTotalDebit_ShouldReturnCorrectAmount() {
        // Arrange
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(testTransaction);
        Transaction anotherTransaction = Transaction.builder()
                .type(TransactionType.STANDARD)
                .amount(200.0)
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(anotherTransaction);

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        // Act
        Double totalDebit = transactionRepository.calculateTotalDebit(
                sourceAccount,
                TransactionType.STANDARD,
                start,
                end
        );

        // Assert
        assertEquals(300.0, totalDebit);
    }

        @Test
    void findAccountTransactions_ShouldReturnAllAccountTransactions() {
        // Arrange
        transactionRepository.save(testTransaction);

        Transaction incomingTransaction = Transaction.builder()
                .type(TransactionType.STANDARD)
                .amount(150.0)
                .sourceAccount(destinationAccount)
                .destinationAccount(sourceAccount)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build();
        transactionRepository.save(incomingTransaction);

        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        // Act
        List<Transaction> transactions = transactionRepository.findAccountTransactions(
                sourceAccount,
                start,
                end
        );

        // Assert
        assertEquals(2, transactions.size());
    }
}