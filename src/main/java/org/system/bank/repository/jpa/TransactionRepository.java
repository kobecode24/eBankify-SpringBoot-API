package org.system.bank.repository.jpa;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;
import org.system.bank.entity.Account;
import org.system.bank.entity.Transaction;
import org.system.bank.entity.User;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccount(Account account);

    List<Transaction> findByDestinationAccount(Account account);

    default List<Transaction> findBySourceAccountOrDestinationAccount(Account account, Account sameAccount) {
        List<Transaction> outgoing = findBySourceAccount(account);
        List<Transaction> incoming = findByDestinationAccount(account);
        outgoing.addAll(incoming);
        return outgoing;
    }

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sourceAccount = :account OR t.destinationAccount = :account) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findAccountTransactions(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.sourceAccount = :account " +
            "AND (:type is null OR t.type = :type) " +
            "AND t.createdAt BETWEEN :startDate AND :endDate " +
            "AND t.status = 'COMPLETED'")
    Double calculateTotalDebit(
            @Param("account") Account account,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = "SELECT t FROM Transaction t",
            countQuery = "SELECT COUNT(t) FROM Transaction t")
    Page<Transaction> findAll(Pageable pageable);

    List<Transaction> findBySourceAccount_UserAndStatus(User user, TransactionStatus status);
}
