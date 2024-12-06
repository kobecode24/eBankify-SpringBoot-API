package org.system.bank.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.entity.User;
import org.system.bank.repository.jpa.LoanRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanEligibilityService {
    private final LoanRepository loanRepository;

    private static final int MIN_AGE = 18;
    private static final int MIN_CREDIT_SCORE = 650;
    private static final double MIN_MONTHLY_INCOME = 3000.0;
    private static final double MAX_LOAN_TO_INCOME_RATIO = 12;
    private static final double MIN_INTEREST_RATE = 5.0;
    private static final double BASE_INTEREST_RATE = 10.0;

    @Data
    @AllArgsConstructor
    public static class EligibilityResult {
        private boolean eligible;
        private List<String> reasons;
    }

    public EligibilityResult checkEligibility(User user, Double requestedAmount) {
        if (user == null || requestedAmount == null) {
            return new EligibilityResult(false, List.of("Invalid user or loan amount"));
        }

        List<String> reasons = new ArrayList<>();

        // Basic criteria validation
        validateBasicCriteria(user, reasons);

        // Active loan check
        if (hasActiveLoan(user)) {
            reasons.add("Cannot have multiple active loans");
        }

        // Loan amount eligibility check
        validateLoanAmount(user, requestedAmount, reasons);

        return new EligibilityResult(reasons.isEmpty(), reasons);
    }

    private void validateBasicCriteria(User user, List<String> reasons) {
        if (user.getAge() < MIN_AGE) {
            reasons.add(String.format("Applicant must be at least %d years old", MIN_AGE));
        }

        if (user.getCreditScore() < MIN_CREDIT_SCORE) {
            reasons.add(String.format("Minimum credit score required is %d", MIN_CREDIT_SCORE));
        }

        if (user.getMonthlyIncome() < MIN_MONTHLY_INCOME) {
            reasons.add(String.format("Minimum monthly income required is $%.2f", MIN_MONTHLY_INCOME));
        }
    }

    private void validateLoanAmount(User user, Double requestedAmount, List<String> reasons) {
        Double totalDebt = calculateTotalDebt(user);
        Double maxLoanAmount = user.getMonthlyIncome() * MAX_LOAN_TO_INCOME_RATIO;
        double currentDebt = totalDebt != null ? totalDebt : 0.0;

        if ((currentDebt + requestedAmount) > maxLoanAmount) {
            reasons.add(String.format(
                    "Maximum loan amount exceeded. Available limit: $%.2f",
                    (maxLoanAmount - currentDebt)
            ));
        }
    }

    @Transactional(readOnly = true)
    public boolean hasActiveLoan(User user) {
        try {
            return user != null && loanRepository.hasActiveLoanApplication(user);
        } catch (Exception e) {
            log.error("Error checking active loan status for user {}: {}", user.getUserId(), e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Double calculateTotalDebt(User user) {
        try {
            Double totalDebt = loanRepository.calculateTotalDebt(user);
            return totalDebt != null ? totalDebt : 0.0;
        } catch (Exception e) {
            log.error("Error calculating total debt for user {}: {}", user.getUserId(), e.getMessage());
            return 0.0;
        }
    }

    public Double calculateInterestRate(User user, Double amount) {
        if (user == null || amount == null) {
            return BASE_INTEREST_RATE;
        }

        double adjustedRate = BASE_INTEREST_RATE;

        // Credit score adjustments
        adjustedRate -= calculateCreditScoreAdjustment(user.getCreditScore());

        // Amount adjustments
        adjustedRate += calculateAmountAdjustment(amount);

        return Math.max(adjustedRate, MIN_INTEREST_RATE);
    }

    private double calculateCreditScoreAdjustment(Integer creditScore) {
        if (creditScore >= 800) return 3.0;
        if (creditScore >= 700) return 2.0;
        if (creditScore >= 650) return 1.0;
        return 0.0;
    }

    private double calculateAmountAdjustment(Double amount) {
        if (amount > 50000) return 1.0;
        if (amount > 25000) return 0.5;
        return 0.0;
    }

    public boolean isEligibleForBasicCriteria(User user) {
        if (user == null) {
            return false;
        }

        return user.getAge() >= MIN_AGE &&
                user.getCreditScore() >= MIN_CREDIT_SCORE &&
                user.getMonthlyIncome() >= MIN_MONTHLY_INCOME;
    }
}