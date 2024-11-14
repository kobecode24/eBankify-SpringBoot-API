// UserRepository.java
package org.system.bank.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.accounts")
    List<User> findAllWithAccounts();

    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.accounts WHERE u IN :users",
            countQuery = "SELECT COUNT(u) FROM User u")
    List<User> findAllWithAccountsByUsers(List<User> users);

    @Query(value = "SELECT u FROM User u",
            countQuery = "SELECT COUNT(u) FROM User u")
    Page<User> findAllPaged(Pageable pageable);

    List<User> findByRole(Role role);

    List<User> findByCreditScoreGreaterThanEqual(Integer creditScore);

    List<User> findByMonthlyIncomeBetween(Double minIncome, Double maxIncome);

    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
