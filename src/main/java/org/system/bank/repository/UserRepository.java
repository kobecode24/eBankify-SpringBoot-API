package org.system.bank.repository;

import org.system.bank.entity.User;
import org.system.bank.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);

    List<User> findByCreditScoreGreaterThanEqual(Integer creditScore);

    List<User> findByMonthlyIncomeBetween(Double minIncome, Double maxIncome);

    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
