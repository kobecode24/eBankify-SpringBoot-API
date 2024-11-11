package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.dto.request.LoanApplicationRequest;
import org.system.bank.dto.response.LoanResponse;
import org.system.bank.entity.Loan;
import org.system.bank.entity.User;
import org.system.bank.enums.LoanStatus;
import org.system.bank.exception.LoanEligibilityException;
import org.system.bank.mapper.LoanMapper;
import org.system.bank.repository.LoanRepository;
import org.system.bank.repository.UserRepository;
import org.system.bank.service.LoanService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final LoanMapper loanMapper;

    @Override
    public LoanResponse createLoan(LoanApplicationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!isEligibleForLoan(user.getUserId(), request.getPrincipal())) {
            throw new IllegalStateException("User is not eligible for this loan");
        }

        Loan loan = loanMapper.toEntity(request);
        loan.setUser(user);
        loan.setInterestRate(calculateInterestRate(user, request.getPrincipal()));
        loan.setMonthlyPayment(calculateInitialMonthlyPayment(request.getPrincipal(), loan.getInterestRate(), request.getTermMonths()));
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(request.getTermMonths()));
        loan.setRemainingAmount(request.getPrincipal());
        loan.setStatus(LoanStatus.PENDING);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toResponse(savedLoan);
    }

    @Override
    public LoanResponse getLoanById(Long id) {
        Loan loan = findLoanById(id);
        return loanMapper.toResponse(loan);
    }

    @Override
    public LoanResponse updateLoan(Long id, LoanApplicationRequest request) {
        Loan existingLoan = findLoanById(id);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Loan loanToUpdate = loanMapper.toEntity(request);
        loanToUpdate.setLoanId(id);
        loanToUpdate.setUser(user);
        loanToUpdate.setStatus(existingLoan.getStatus());
        loanToUpdate.setInterestRate(calculateInterestRate(user, request.getPrincipal()));
        loanToUpdate.setMonthlyPayment(calculateInitialMonthlyPayment(request.getPrincipal(), loanToUpdate.getInterestRate(), request.getTermMonths()));

        Loan updatedLoan = loanRepository.save(loanToUpdate);
        return loanMapper.toResponse(updatedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getAllLoans() {
        return loanMapper.toResponseList(loanRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return loanMapper.toResponseList(loanRepository.findByUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getLoansByStatus(LoanStatus status) {
        return loanMapper.toResponseList(loanRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalDebt(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return loanRepository.calculateTotalDebt(user);
    }

    @Override
    public Double calculateMonthlyPayment(Long loanId) {
        Loan loan = findLoanById(loanId);
        return calculateInitialMonthlyPayment(loan.getPrincipal(), loan.getInterestRate(), loan.getTermMonths());
    }

    @Override
    public boolean isEligibleForLoan(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        validateAge(user.getAge());
        validateCreditScore(user.getCreditScore());
        validateIncome(user.getMonthlyIncome());

        Double totalDebt = calculateTotalDebt(userId);
        return isEligibleForLoanAmount(user, amount, totalDebt);
    }

    private boolean isEligibleForLoanAmount(User user, Double amount, Double totalDebt) {
        double maxLoanAmount = user.getMonthlyIncome() * 0.5;
        return amount <= maxLoanAmount && amount + totalDebt <= maxLoanAmount;
    }

    private void validateAge(Integer age) {
        if (age < 18) {
            throw new LoanEligibilityException("Applicant must be at least 18 years old");
        }
    }

    private void validateCreditScore(Integer creditScore) {
        if (creditScore < 650) {
            throw new LoanEligibilityException("Credit score must be at least 650");
        }
    }

    private void validateIncome(Double monthlyIncome) {
        if (monthlyIncome < 3000.0) {
            throw new LoanEligibilityException("Minimum monthly income requirement not met");
        }
    }

    @Override
    public LoanResponse processLoanPayment(Long loanId, Double amount) {
        Loan loan = findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not active");
        }

        if (amount <= 0 || amount > loan.getRemainingAmount()) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        double remainingAmount = loan.getRemainingAmount() - amount;
        loan.setRemainingAmount(remainingAmount);

        if (remainingAmount <= 0) {
            loan.setStatus(LoanStatus.COMPLETED);
        }

        Loan updatedLoan = loanRepository.save(loan);
        return loanMapper.toResponse(updatedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getOverdueLoans() {
        return loanMapper.toResponseList(loanRepository.findOverdueLoans(LocalDate.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveLoan(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return loanRepository.hasActiveLoanApplication(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getDefaultedLoansCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return loanRepository.countDefaultedLoans(user);
    }

    @Override
    public LoanResponse approveLoan(Long loanId) {
        Loan loan = findLoanById(loanId);
        validateLoanStatus(loan, LoanStatus.PENDING, "Loan is not in PENDING state");
        loan.setStatus(LoanStatus.APPROVED);
        Loan updatedLoan = loanRepository.save(loan);
        return loanMapper.toResponse(updatedLoan);
    }

    @Override
    public LoanResponse rejectLoan(Long loanId) {
        Loan loan = findLoanById(loanId);
        validateLoanStatus(loan, LoanStatus.PENDING, "Loan is not in PENDING state");
        loan.setStatus(LoanStatus.REJECTED);
        Loan updatedLoan = loanRepository.save(loan);
        return loanMapper.toResponse(updatedLoan);
    }

    private Loan findLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found with id: " + id));
    }

    private void validateLoanStatus(Loan loan, LoanStatus expectedStatus, String errorMessage) {
        if (loan.getStatus() != expectedStatus) {
            throw new IllegalStateException(errorMessage);
        }
    }

    private Double calculateInitialMonthlyPayment(Double principal, Double annualInterestRate, Integer termMonths) {
        double monthlyRate = annualInterestRate / 12.0 / 100.0;
        return principal * (monthlyRate * Math.pow(1 + monthlyRate, termMonths))
                / (Math.pow(1 + monthlyRate, termMonths) - 1);
    }

    private Double calculateInterestRate(User user, Double amount) {
        double baseRate = 10.0;

        // Credit score adjustments
        if (user.getCreditScore() >= 800) {
            baseRate -= 3.0;
        } else if (user.getCreditScore() >= 700) {
            baseRate -= 2.0;
        } else if (user.getCreditScore() >= 650) {
            baseRate -= 1.0;
        }

        // Loan amount adjustments
        if (amount > 50000) {
            baseRate += 1.0;
        } else if (amount > 25000) {
            baseRate += 0.5;
        }

        // Ensure minimum rate
        return Math.max(baseRate, 5.0);
    }

    @Override
    public Double calculateInterestRate(Long userId, Double amount) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        double baseRate = 10.0;

        // Credit score adjustments
        if (user.getCreditScore() >= 800) {
            baseRate -= 3.0;
        } else if (user.getCreditScore() >= 700) {
            baseRate -= 2.0;
        } else if (user.getCreditScore() >= 650) {
            baseRate -= 1.0;
        }

        // Loan amount adjustments
        if (amount > 50000) {
            baseRate += 1.0;
        } else if (amount > 25000) {
            baseRate += 0.5;
        }

        // Ensure minimum rate
        return Math.max(baseRate, 5.0);
    }
}
