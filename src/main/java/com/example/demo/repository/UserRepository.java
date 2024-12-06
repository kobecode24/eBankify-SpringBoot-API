package com.example.demo.repository;


import com.example.demo.entities.User;
import com.example.demo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByCreditScoreGreaterThanEqual(Integer creditScore);

    List<User> findByMonthlyIncomeBetween(Double minIncome, Double maxIncome);

    List<User> findByAgeBetween(Integer minAge, Integer maxAge);

    boolean existsByEmail(String email);
}
