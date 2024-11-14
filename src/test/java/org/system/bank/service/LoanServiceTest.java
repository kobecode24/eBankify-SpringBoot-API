package org.system.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.dto.request.LoanApplicationRequest;
import org.system.bank.dto.response.LoanResponse;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.Loan;
import org.system.bank.entity.User;
import org.system.bank.enums.LoanStatus;
import org.system.bank.mapper.LoanMapper;
import org.system.bank.repository.jpa.LoanRepository;
import org.system.bank.repository.jpa.UserRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.LoanServiceImpl;
import org.system.bank.util.TestDataBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LoanServiceTest extends BaseServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    private User testUser;
    private Loan testLoan;
    private LoanApplicationRequest testRequest;
    private LoanResponse testResponse;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createTestUser();
        testLoan = TestDataBuilder.createTestLoan();
        testRequest = TestDataBuilder.createTestLoanApplicationRequest();

        testUserResponse = UserResponse.builder()
                .userId(testUser.getUserId())
                .name(testUser.getName())
                .email(testUser.getEmail())
                .build();

        testResponse = LoanResponse.builder()
                .loanId(testLoan.getLoanId())
                .principal(testLoan.getPrincipal())
                .interestRate(testLoan.getInterestRate())
                .termMonths(testLoan.getTermMonths())
                .monthlyPayment(testLoan.getMonthlyPayment())
                .remainingAmount(testLoan.getRemainingAmount())
                .status(testLoan.getStatus())
                .userId(testUser.getUserId())
                .userName(testUser.getName())
                .startDate(testLoan.getStartDate())
                .endDate(testLoan.getEndDate())
                .guarantees(testLoan.getGuarantees())
                .build();
    }

    @Test
    void createLoan_ShouldReturnLoanResponse_WhenUserIsEligible() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(loanMapper.toEntity(any(LoanApplicationRequest.class))).thenReturn(testLoan);
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(testResponse);

        // Act
        LoanResponse result = loanService.createLoan(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testLoan.getPrincipal(), result.getPrincipal());
        assertEquals(LoanStatus.PENDING, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void createLoan_ShouldThrowException_WhenUserNotEligible() {
        // Arrange
        testUser.setCreditScore(500); // Set low credit score
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> loanService.createLoan(testRequest));
    }

    @Test
    void approveLoan_ShouldUpdateStatus() {
        // Arrange
        testLoan.setStatus(LoanStatus.PENDING);
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testLoan));

        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan savedLoan = invocation.getArgument(0);
            savedLoan.setStatus(LoanStatus.APPROVED);
            return savedLoan;
        });

        when(loanMapper.toResponse(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            return LoanResponse.builder()
                    .loanId(loan.getLoanId())
                    .status(loan.getStatus())
                    .build();
        });

        // Act
        LoanResponse result = loanService.approveLoan(1L);

        // Assert
        assertEquals(LoanStatus.APPROVED, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void rejectLoan_ShouldUpdateStatus() {
        // Arrange
        testLoan.setStatus(LoanStatus.PENDING);
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testLoan));

        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan savedLoan = invocation.getArgument(0);
            savedLoan.setStatus(LoanStatus.REJECTED);
            return savedLoan;
        });

        when(loanMapper.toResponse(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            return LoanResponse.builder()
                    .loanId(loan.getLoanId())
                    .status(loan.getStatus())
                    .build();
        });

        // Act
        LoanResponse result = loanService.rejectLoan(1L);

        // Assert
        assertEquals(LoanStatus.REJECTED, result.getStatus());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void processLoanPayment_ShouldUpdateRemainingAmount() {
        // Arrange
        testLoan.setStatus(LoanStatus.ACTIVE);
        testLoan.setRemainingAmount(1000.0);
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testLoan));
        when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(testResponse);

        // Act
        LoanResponse result = loanService.processLoanPayment(1L, 500.0);

        // Assert
        assertEquals(500.0, testLoan.getRemainingAmount());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void processLoanPayment_ShouldCompleteLoad_WhenFullyPaid() {
        // Arrange
        testLoan.setStatus(LoanStatus.ACTIVE);
        testLoan.setRemainingAmount(500.0);
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan savedLoan = invocation.getArgument(0);
            savedLoan.setStatus(LoanStatus.COMPLETED);
            savedLoan.setRemainingAmount(0.0);
            return savedLoan;
        });
        when(loanMapper.toResponse(any(Loan.class))).thenReturn(testResponse);

        // Act
        LoanResponse result = loanService.processLoanPayment(1L, 500.0);

        // Assert
        assertEquals(LoanStatus.COMPLETED, testLoan.getStatus());
        assertEquals(0.0, testLoan.getRemainingAmount());
        verify(loanRepository).save(any(Loan.class));
    }

    @Test
    void getOverdueLoans_ShouldReturnOverdueLoans() {
        // Arrange
        List<Loan> overdueLoans = Arrays.asList(testLoan);
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(overdueLoans);
        when(loanMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse));

        // Act
        List<LoanResponse> result = loanService.getOverdueLoans();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(loanRepository).findOverdueLoans(any(LocalDate.class));
    }

    @Test
    void calculateTotalDebt_ShouldReturnCorrectAmount() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(loanRepository.calculateTotalDebt(any(User.class))).thenReturn(5000.0);

        // Act
        Double result = loanService.calculateTotalDebt(1L);

        // Assert
        assertEquals(5000.0, result);
        verify(loanRepository).calculateTotalDebt(any(User.class));
    }

    @Test
    void calculateMonthlyPayment_ShouldReturnCorrectAmount() {
        // Arrange
        when(loanRepository.findById(anyLong())).thenReturn(Optional.of(testLoan));

        // Act
        Double result = loanService.calculateMonthlyPayment(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result > 0);
        verify(loanRepository).findById(anyLong());
    }

    @Test
    void hasActiveLoan_ShouldReturnTrue_WhenUserHasActiveLoan() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(loanRepository.hasActiveLoanApplication(any(User.class))).thenReturn(true);

        // Act
        boolean result = loanService.hasActiveLoan(1L);

        // Assert
        assertTrue(result);
        verify(loanRepository).hasActiveLoanApplication(any(User.class));
    }

    @Test
    void getDefaultedLoansCount_ShouldReturnCount() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(loanRepository.countDefaultedLoans(any(User.class))).thenReturn(2L);

        // Act
        Long result = loanService.getDefaultedLoansCount(1L);

        // Assert
        assertEquals(2L, result);
        verify(loanRepository).countDefaultedLoans(any(User.class));
    }
}