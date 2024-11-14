package org.system.bank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.system.bank.repository.base.BaseRepositoryTest;
import org.system.bank.entity.Loan;
import org.system.bank.entity.User;
import org.system.bank.enums.LoanStatus;
import org.system.bank.enums.Role;
import org.system.bank.repository.jpa.LoanRepository;
import org.system.bank.repository.jpa.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoanRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Loan testLoan;

    @BeforeEach
    void setUp() {
        loanRepository.deleteAll();
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

        testLoan = Loan.builder()
                .principal(10000.0)
                .interestRate(5.0)
                .termMonths(12)
                .monthlyPayment(856.0)
                .remainingAmount(10000.0)
                .status(LoanStatus.PENDING)
                .user(testUser)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .guarantees("Property Deed")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByUser_ShouldReturnLoans() {
        // Arrange
        loanRepository.save(testLoan);

        // Act
        List<Loan> loans = loanRepository.findByUser(testUser);

        // Assert
        assertEquals(1, loans.size());
        assertEquals(testLoan.getPrincipal(), loans.get(0).getPrincipal());
    }

    @Test
    void findByStatus_ShouldReturnLoans() {
        // Arrange
        loanRepository.save(testLoan);

        // Act
        List<Loan> pendingLoans = loanRepository.findByStatus(LoanStatus.PENDING);
        List<Loan> activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE);

        // Assert
        assertEquals(1, pendingLoans.size());
        assertTrue(activeLoans.isEmpty());
    }

    @Test
    void calculateTotalDebt_ShouldReturnCorrectAmount() {
        // Arrange
        testLoan.setStatus(LoanStatus.ACTIVE);
        loanRepository.save(testLoan);

        Loan anotherLoan = Loan.builder()
                .principal(5000.0)
                .interestRate(5.0)
                .termMonths(12)
                .monthlyPayment(428.0)
                .remainingAmount(5000.0)
                .status(LoanStatus.ACTIVE)
                .user(testUser)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(12))
                .build();
        loanRepository.save(anotherLoan);

        // Act
        Double totalDebt = loanRepository.calculateTotalDebt(testUser);

        // Assert
        assertEquals(15000.0, totalDebt);
    }

    @Test
    void findOverdueLoans_ShouldReturnOverdueLoans() {
        // Arrange
        testLoan.setStatus(LoanStatus.ACTIVE);
        testLoan.setEndDate(LocalDate.now().minusDays(1));
        loanRepository.save(testLoan);

        // Act
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());

        // Assert
        assertEquals(1, overdueLoans.size());
    }

    @Test
    void hasActiveLoanApplication_ShouldReturnTrue_WhenUserHasActiveLoan() {
        // Arrange
        loanRepository.save(testLoan);

        // Act
        boolean hasActiveLoan = loanRepository.hasActiveLoanApplication(testUser);

        // Assert
        assertTrue(hasActiveLoan);
    }

    @Test
    void countDefaultedLoans_ShouldReturnCorrectCount() {
        // Arrange
        testLoan.setStatus(LoanStatus.DEFAULTED);
        loanRepository.save(testLoan);

        // Act
        Long defaultedCount = loanRepository.countDefaultedLoans(testUser);

        // Assert
        assertEquals(1L, defaultedCount);
    }
}
