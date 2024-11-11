package org.system.bank.util;

import org.system.bank.dto.request.*;
import org.system.bank.dto.response.*;
import org.system.bank.entity.*;
import org.system.bank.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestDataBuilder {

    public static User createTestUser() {
        return User.builder()
                .userId(1L)
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword123")
                .age(30)
                .monthlyIncome(5000.0)
                .creditScore(750)
                .role(Role.USER)
                .build();
    }

    public static Account createTestAccount() {
        return Account.builder()
                .accountId(1L)
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .user(createTestUser())
                .build();
    }

    public static Transaction createTestTransaction() {
        return Transaction.builder()
                .transactionId(1L)
                .type(TransactionType.STANDARD)
                .amount(100.0)
                .sourceAccount(createTestAccount())
                .destinationAccount(createTestAccount())
                .status(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Loan createTestLoan() {
        return Loan.builder()
                .loanId(1L)
                .principal(10000.0)
                .interestRate(5.0)
                .termMonths(12)
                .monthlyPayment(856.0)
                .remainingAmount(10000.0)
                .status(LoanStatus.PENDING)
                .user(createTestUser())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .build();
    }

    public static UserRegistrationRequest createTestUserRegistrationRequest() {
        return UserRegistrationRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("Test@123!")
                .age(30)
                .monthlyIncome(5000.0)
                .creditScore(750)
                .role(Role.USER)
                .build();
    }

    public static TransactionRequest createTestTransactionRequest() {
        return TransactionRequest.builder()
                .amount(100.0)
                .sourceAccountId(1L)
                .destinationAccountId(2L)
                .type(TransactionType.STANDARD)
                .build();
    }

    public static AccountCreationRequest createTestAccountCreationRequest() {
        return AccountCreationRequest.builder()
                .userId(1L)
                .initialDeposit(1000.0)
                .build();
    }

    // For Invoice related test data
    public static Invoice createTestInvoice() {
        return Invoice.builder()
                .invoiceId(1L)
                .amountDue(100.0)
                .dueDate(LocalDate.now().plusDays(7))
                .status(InvoiceStatus.PENDING)
                .user(createTestUser())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static InvoiceCreationRequest createTestInvoiceCreationRequest() {
        return InvoiceCreationRequest.builder()
                .userId(1L)
                .amountDue(100.0)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    // For Loan related test data
    public static LoanApplicationRequest createTestLoanApplicationRequest() {
        return LoanApplicationRequest.builder()
                .userId(1L)
                .principal(10000.0)
                .termMonths(12)
                .guarantees("Property Deed")
                .build();
    }

    public static UserResponse createTestUserResponse() {
        return UserResponse.builder()
                .userId(1L)
                .name("Test User")
                .email("test@example.com")
                .age(30)
                .monthlyIncome(5000.0)
                .creditScore(750)
                .role(Role.USER)
                .accounts(new ArrayList<>())
                .build();
    }

    public static AccountResponse createTestAccountResponse() {
        return AccountResponse.builder()
                .accountId(1L)
                .balance(1000.0)
                .status(AccountStatus.ACTIVE)
                .userId(1L)
                .userName("Test User")
                .build();
    }

    public static TransactionResponse createTestTransactionResponse() {
        return TransactionResponse.builder()
                .transactionId(1L)
                .amount(100.0)
                .type(TransactionType.STANDARD)
                .status(TransactionStatus.PENDING)
                .sourceAccountId(1L)
                .destinationAccountId(2L)
                .createdAt(LocalDateTime.now())
                .fee(0.1) // 0.1% for standard transactions
                .build();
    }

    public static LoanResponse createTestLoanResponse() {
        return LoanResponse.builder()
                .loanId(1L)
                .principal(10000.0)
                .interestRate(5.0)
                .termMonths(12)
                .monthlyPayment(856.0)
                .remainingAmount(10000.0)
                .status(LoanStatus.PENDING)
                .userId(1L)
                .userName("Test User")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .guarantees("Property Deed")
                .build();
    }

    public static InvoiceResponse createTestInvoiceResponse() {
        return InvoiceResponse.builder()
                .invoiceId(1L)
                .amountDue(100.0)
                .dueDate(LocalDate.now().plusDays(7))
                .status(InvoiceStatus.PENDING)
                .userId(1L)
                .userName("Test User")
                .build();
    }
}