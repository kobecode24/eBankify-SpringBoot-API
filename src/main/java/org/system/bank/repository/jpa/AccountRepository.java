package org.system.bank.repository.jpa;

import org.system.bank.entity.Account;
import org.system.bank.entity.User;
import org.system.bank.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findByUserAndStatus(User user, AccountStatus status);

    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance")
    List<Account> findAccountsWithBalanceGreaterThan(@Param("minBalance") Double minBalance);

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.user = :user AND a.status = 'ACTIVE'")
    boolean hasActiveAccount(@Param("user") User user);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user = :user AND a.status = 'ACTIVE'")
    Double getTotalBalance(@Param("user") User user);
}
