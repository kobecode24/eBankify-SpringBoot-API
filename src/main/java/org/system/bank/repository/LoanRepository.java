package org.system.bank.repository;

import org.system.bank.entity.Loan;
import org.system.bank.entity.User;
import org.system.bank.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUser(User user);

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByUserAndStatus(User user, LoanStatus status);

    @Query("SELECT SUM(l.remainingAmount) FROM Loan l " +
            "WHERE l.user = :user AND l.status = 'ACTIVE'")
    Double calculateTotalDebt(@Param("user") User user);

    @Query("SELECT SUM(l.monthlyPayment) FROM Loan l " +
            "WHERE l.user = :user AND l.status = 'ACTIVE'")
    Double calculateTotalMonthlyPayments(@Param("user") User user);

    @Query("SELECT l FROM Loan l " +
            "WHERE l.status = 'ACTIVE' " +
            "AND l.endDate < :date " +
            "AND l.remainingAmount > 0")
    List<Loan> findOverdueLoans(@Param("date") LocalDate date);

    @Query("SELECT COUNT(l) > 0 FROM Loan l " +
            "WHERE l.user = :user " +
            "AND l.status IN ('PENDING', 'ACTIVE')")
    boolean hasActiveLoanApplication(@Param("user") User user);

    @Query("SELECT COUNT(l) FROM Loan l " +
            "WHERE l.user = :user AND l.status = 'DEFAULTED'")
    Long countDefaultedLoans(@Param("user") User user);
}
