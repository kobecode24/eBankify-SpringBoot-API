package org.system.bank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.system.bank.repository.base.BaseRepositoryTest;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;
import org.system.bank.repository.jpa.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("hashedPassword123")
                .age(30)
                .monthlyIncome(5000.0)
                .creditScore(750)
                .role(Role.USER)
                .build();
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User savedUser = userRepository.save(testUser);

        // Act
        Optional<User> foundUser = userRepository.findByEmail(testUser.getEmail());

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
        assertEquals(savedUser.getName(), foundUser.get().getName());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findByRole_ShouldReturnUsers_WhenUsersExist() {
        // Arrange
        userRepository.save(testUser);
        User anotherUser = User.builder()
                .name("Another User")
                .email("another@example.com")
                .password("hashedPassword123")
                .age(25)
                .monthlyIncome(4000.0)
                .creditScore(700)
                .role(Role.USER)
                .build();
        userRepository.save(anotherUser);

        // Act
        List<User> users = userRepository.findByRole(Role.USER);

        // Assert
        assertEquals(2, users.size());
        assertTrue(users.stream().allMatch(user -> user.getRole() == Role.USER));
    }

    @Test
    void findByCreditScoreGreaterThanEqual_ShouldReturnQualifiedUsers() {
        // Arrange
        userRepository.save(testUser);
        User lowScoreUser = User.builder()
                .name("Low Score User")
                .email("lowscore@example.com")
                .password("hashedPassword123")
                .age(25)
                .monthlyIncome(4000.0)
                .creditScore(600)
                .role(Role.USER)
                .build();
        userRepository.save(lowScoreUser);

        // Act
        List<User> qualifiedUsers = userRepository.findByCreditScoreGreaterThanEqual(700);

        // Assert
        assertEquals(1, qualifiedUsers.size());
        assertTrue(qualifiedUsers.stream().allMatch(user -> user.getCreditScore() >= 700));
    }

    @Test
    void findByMonthlyIncomeBetween_ShouldReturnUsersInRange() {
        // Arrange
        userRepository.save(testUser);
        User highIncomeUser = User.builder()
                .name("High Income User")
                .email("highincome@example.com")
                .password("hashedPassword123")
                .age(35)
                .monthlyIncome(8000.0)
                .creditScore(800)
                .role(Role.USER)
                .build();
        userRepository.save(highIncomeUser);

        // Act
        List<User> users = userRepository.findByMonthlyIncomeBetween(4000.0, 6000.0);

        // Assert
        assertEquals(1, users.size());
        assertTrue(users.stream()
                .allMatch(user -> user.getMonthlyIncome() >= 4000.0
                                  && user.getMonthlyIncome() <= 6000.0));
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        userRepository.save(testUser);

        // Act
        boolean exists = userRepository.existsByEmail(testUser.getEmail());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }
}