package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.entity.User;
import org.system.bank.repository.LoanRepository;

@Service
@RequiredArgsConstructor
public class LoanEligibilityService {
    private final LoanRepository loanRepository;

    public boolean isEligibleForBasicCriteria(User user) {
        if (user.getAge() < 18) return false;
        if (user.getCreditScore() < 650) return false;
        return user.getMonthlyIncome() >= 3000.0;
    }

    @Transactional(readOnly = true)
    public boolean hasActiveLoan(User user) {
        return loanRepository.hasActiveLoanApplication(user);
    }

    @Transactional(readOnly = true)
    public Double calculateTotalDebt(User user) {
        return loanRepository.calculateTotalDebt(user);
    }

    public boolean isEligibleForLoanAmount(User user, Double requestedAmount, Double totalDebt) {
        if (totalDebt == null) totalDebt = 0.0;
        return (totalDebt + requestedAmount) <= user.getMonthlyIncome() * 12;
    }

    public Double calculateInterestRate(User user, Double amount) {
        double baseRate = 10.0;

        if (user.getCreditScore() >= 800) {
            baseRate -= 3.0;
        } else if (user.getCreditScore() >= 700) {
            baseRate -= 2.0;
        } else if (user.getCreditScore() >= 650) {
            baseRate -= 1.0;
        }

        if (amount > 50000) {
            baseRate += 1.0;
        } else if (amount > 25000) {
            baseRate += 0.5;
        }

        return Math.max(baseRate, 5.0);
    }
}
