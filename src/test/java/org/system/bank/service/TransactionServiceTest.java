// src/test/java/org/system/bank/service/TransactionServiceTest.java
package org.system.bank.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.entity.Account;
import org.system.bank.entity.Transaction;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;
import org.system.bank.mapper.TransactionMapper;
import org.system.bank.repository.jpa.TransactionRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.TransactionServiceImpl;
import org.system.bank.util.TestDataBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionServiceTest extends BaseServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction testTransaction;
    private TransactionRequest testRequest;
    private TransactionResponse testResponse;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        sourceAccount = TestDataBuilder.createTestAccount();
        sourceAccount.setBalance(2000.0);

        destinationAccount = TestDataBuilder.createTestAccount();
        destinationAccount.setAccountId(2L);

        testTransaction = TestDataBuilder.createTestTransaction();
        testRequest = TestDataBuilder.createTestTransactionRequest();

        testResponse = TransactionResponse.builder()
                .transactionId(1L)
                .amount(testRequest.getAmount())
                .type(testRequest.getType())
                .status(TransactionStatus.COMPLETED)
                .sourceAccountId(testRequest.getSourceAccountId())
                .destinationAccountId(testRequest.getDestinationAccountId())
                .createdAt(LocalDateTime.now())
                .fee(0.1)
                .build();
    }

    @Test
    void createTransaction_ShouldReturnTransactionResponse() {
        // Arrange
        when(accountService.getAccountEntity(eq(testRequest.getSourceAccountId())))
                .thenReturn(sourceAccount);
        when(accountService.getAccountEntity(eq(testRequest.getDestinationAccountId())))
                .thenReturn(destinationAccount);

        when(transactionMapper.toEntity(any(TransactionRequest.class)))
                .thenReturn(testTransaction);

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction savedTransaction = invocation.getArgument(0);
                    savedTransaction.setTransactionId(1L);
                    return savedTransaction;
                });

        when(transactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(testTransaction));

        when(transactionMapper.toResponse(any(Transaction.class)))
                .thenReturn(testResponse);

        when(accountService.saveAccount(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        TransactionResponse result = transactionService.createTransaction(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testResponse.getAmount(), result.getAmount());
        assertEquals(testResponse.getStatus(), result.getStatus());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountService, times(2)).saveAccount(any(Account.class));
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        when(transactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toResponse(any(Transaction.class)))
                .thenReturn(testResponse);

        // Act
        TransactionResponse result = transactionService.getTransactionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testResponse.getAmount(), result.getAmount());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void getTransactionById_ShouldThrowException_WhenTransactionNotFound() {
        // Arrange
        when(transactionRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionById(1L));
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction, testTransaction);
        when(transactionRepository.findAll())
                .thenReturn(transactions);
        when(transactionMapper.toResponseList(anyList()))
                .thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<TransactionResponse> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository).findAll();
    }

    @Test
    void getTransactionsByAccount_ShouldReturnAccountTransactions() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction, testTransaction);
        when(accountService.getAccountEntity(anyLong()))
                .thenReturn(sourceAccount);
        when(transactionRepository.findBySourceAccountOrDestinationAccount(any(), any()))
                .thenReturn(transactions);
        when(transactionMapper.toResponseList(anyList()))
                .thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<TransactionResponse> result = transactionService.getTransactionsByAccount(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository).findBySourceAccountOrDestinationAccount(any(), any());
    }

    @Test
    void processTransaction_ShouldProcessSuccessfully() {
        // Arrange
        testTransaction.setStatus(TransactionStatus.PENDING);
        when(transactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(testTransaction));
        when(accountService.getAccountEntity(anyLong()))
                .thenReturn(sourceAccount);
        when(accountService.saveAccount(any(Account.class)))
                .thenReturn(sourceAccount);
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        // Act
        transactionService.processTransaction(1L);

        // Assert
        verify(accountService, times(2)).saveAccount(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void processTransaction_ShouldThrowException_WhenTransactionNotPending() {
        // Arrange
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        when(transactionRepository.findById(anyLong()))
                .thenReturn(Optional.of(testTransaction));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> transactionService.processTransaction(1L));
    }

    @Test
    void isTransactionValid_ShouldReturnTrue_WhenValidTransaction() {
        // Arrange
        when(accountService.getAccountEntity(eq(testRequest.getSourceAccountId())))
                .thenReturn(sourceAccount);
        when(accountService.getAccountEntity(eq(testRequest.getDestinationAccountId())))
                .thenReturn(destinationAccount);

        // Act
        boolean result = transactionService.isTransactionValid(testRequest);

        // Assert
        assertTrue(result);
    }

    @Test
    void isTransactionValid_ShouldReturnFalse_WhenInsufficientFunds() {
        // Arrange
        sourceAccount.setBalance(50.0);
        when(accountService.getAccountEntity(eq(testRequest.getSourceAccountId())))
                .thenReturn(sourceAccount);
        when(accountService.getAccountEntity(eq(testRequest.getDestinationAccountId())))
                .thenReturn(destinationAccount);

        // Act
        boolean result = transactionService.isTransactionValid(testRequest);

        // Assert
        assertFalse(result);
    }

    @Test
    void calculateTransactionFee_ShouldReturnCorrectFee() {
        // Arrange
        TransactionRequest standardRequest = TransactionRequest.builder()
                .amount(100.0)
                .type(TransactionType.STANDARD)
                .build();

        TransactionRequest instantRequest = TransactionRequest.builder()
                .amount(100.0)
                .type(TransactionType.INSTANT)
                .build();

        // Act
        Double standardFee = transactionService.calculateTransactionFee(standardRequest);
        Double instantFee = transactionService.calculateTransactionFee(instantRequest);

        // Assert
        assertEquals(0.1, standardFee); // 0.1% for standard
        assertEquals(0.5, instantFee);  // 0.5% for instant
    }
}
