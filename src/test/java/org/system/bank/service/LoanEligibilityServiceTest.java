package org.system.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.entity.User;
import org.system.bank.repository.LoanRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.LoanEligibilityService;
import org.system.bank.util.TestDataBuilder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoanEligibilityServiceTest extends BaseServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanEligibilityService loanEligibilityService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createTestUser();
    }

    @Test
    void isEligibleForBasicCriteria_ShouldReturnTrue_WhenAllCriteriaMet() {
        // Arrange
        testUser.setAge(30);
        testUser.setCreditScore(700);
        testUser.setMonthlyIncome(5000.0);

        // Act
        boolean result = loanEligibilityService.isEligibleForBasicCriteria(testUser);

        // Assert
        assertTrue(result);
    }

    @Test
    void isEligibleForBasicCriteria_ShouldReturnFalse_WhenAgeTooLow() {
        // Arrange
        testUser.setAge(17);
        testUser.setCreditScore(700);
        testUser.setMonthlyIncome(5000.0);

        // Act
        boolean result = loanEligibilityService.isEligibleForBasicCriteria(testUser);

        // Assert
        assertFalse(result);
    }

    @Test
    void isEligibleForBasicCriteria_ShouldReturnFalse_WhenCreditScoreTooLow() {
        // Arrange
        testUser.setAge(30);
        testUser.setCreditScore(600);
        testUser.setMonthlyIncome(5000.0);

        // Act
        boolean result = loanEligibilityService.isEligibleForBasicCriteria(testUser);

        // Assert
        assertFalse(result);
    }

    @Test
    void isEligibleForBasicCriteria_ShouldReturnFalse_WhenIncomeTooLow() {
        // Arrange
        testUser.setAge(30);
        testUser.setCreditScore(700);
        testUser.setMonthlyIncome(2000.0);

        // Act
        boolean result = loanEligibilityService.isEligibleForBasicCriteria(testUser);

        // Assert
        assertFalse(result);
    }

    @Test
    void hasActiveLoan_ShouldReturnTrue_WhenUserHasActiveLoan() {
        // Arrange
        when(loanRepository.hasActiveLoanApplication(any(User.class))).thenReturn(true);

        // Act
        boolean result = loanEligibilityService.hasActiveLoan(testUser);

        // Assert
        assertTrue(result);
    }

    @Test
    void isEligibleForLoanAmount_ShouldReturnTrue_WhenAmountWithinLimit() {
        // Arrange
        double requestedAmount = 20000.0;
        double currentDebt = 10000.0;
        testUser.setMonthlyIncome(5000.0); // Annual income = 60000

        // Act
        boolean result = loanEligibilityService.isEligibleForLoanAmount(testUser, requestedAmount, currentDebt);

        // Assert
        assertTrue(result);
    }

    @Test
    void isEligibleForLoanAmount_ShouldReturnFalse_WhenAmountExceedsLimit() {
        // Arrange
        double requestedAmount = 50000.0;
        double currentDebt = 20000.0;
        testUser.setMonthlyIncome(5000.0); // Annual income = 60000

        // Act
        boolean result = loanEligibilityService.isEligibleForLoanAmount(testUser, requestedAmount, currentDebt);

        // Assert
        assertFalse(result);
    }

    @Test
    void calculateInterestRate_ShouldReturnBaseRate_ForAverageProfile() {
        // Arrange
        testUser.setCreditScore(700);
        double amount = 10000.0;

        // Act
        double rate = loanEligibilityService.calculateInterestRate(testUser, amount);

        // Assert
        assertEquals(8.0, rate); // Base rate (10%) – 2% for credit score
    }

    @Test
    void calculateInterestRate_ShouldReturnLowerRate_ForExcellentProfile() {
        // Arrange
        testUser.setCreditScore(800);
        double amount = 10000.0;

        // Act
        double rate = loanEligibilityService.calculateInterestRate(testUser, amount);

        // Assert
        assertEquals(7.0, rate); // Base rate (10%) – 3% for excellent credit score
    }

    @Test
    void calculateInterestRate_ShouldReturnHigherRate_ForLargeAmount() {
        // Arrange
        testUser.setCreditScore(700);
        double amount = 60000.0;

        // Act
        double rate = loanEligibilityService.calculateInterestRate(testUser, amount);

        // Assert
        assertEquals(9.0, rate); // Base rate (10%) – 2% for credit score + 1% for large amount
    }
}