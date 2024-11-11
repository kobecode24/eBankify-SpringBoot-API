// src/test/java/org/system/bank/service/UserServiceTest.java
package org.system.bank.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;
import org.system.bank.mapper.UserMapper;
import org.system.bank.repository.UserRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.LoanEligibilityService;
import org.system.bank.service.impl.UserServiceImpl;
import org.system.bank.util.TestDataBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceTest extends BaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LoanEligibilityService loanEligibilityService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserRegistrationRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createTestUser();
        testRequest = TestDataBuilder.createTestUserRegistrationRequest();
        testResponse = UserResponse.builder()
                .userId(testUser.getUserId())
                .name(testUser.getName())
                .email(testUser.getEmail())
                .age(testUser.getAge())
                .monthlyIncome(testUser.getMonthlyIncome())
                .creditScore(testUser.getCreditScore())
                .role(testUser.getRole())
                .build();
    }

    @Test
    void createUser_ShouldReturnUserResponse() {
        // Arrange
        when(userMapper.toEntity(any(UserRegistrationRequest.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testResponse);

        // Act
        UserResponse result = userService.createUser(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(any(User.class))).thenReturn(testResponse);

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userMapper.toEntity(any(UserRegistrationRequest.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testResponse);

        // Act
        UserResponse result = userService.updateUser(1L, testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUsersByRole_ShouldReturnUsersWithSpecificRole() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser);
        when(userRepository.findByRole(any(Role.class))).thenReturn(users);
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<UserResponse> result = userService.getUsersByRole(Role.USER);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByRole(Role.USER);
    }

    @Test
    void isEligibleForLoan_ShouldReturnTrue_WhenUserIsEligible() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(loanEligibilityService.isEligibleForBasicCriteria(any(User.class))).thenReturn(true);
        when(loanEligibilityService.hasActiveLoan(any(User.class))).thenReturn(false);

        // Act
        boolean result = userService.isEligibleForLoan(1L);

        // Assert
        assertTrue(result);
        verify(loanEligibilityService).isEligibleForBasicCriteria(any(User.class));
    }

    @Test
    void isEligibleForLoan_ShouldReturnFalse_WhenUserHasActiveLoan() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(loanEligibilityService.isEligibleForBasicCriteria(any(User.class))).thenReturn(true);
        when(loanEligibilityService.hasActiveLoan(any(User.class))).thenReturn(true);

        // Act
        boolean result = userService.isEligibleForLoan(1L);

        // Assert
        assertFalse(result);
        verify(loanEligibilityService).hasActiveLoan(any(User.class));
    }

    @Test
    void getUsersByAgeRange_ShouldReturnUsersInRange() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser);
        when(userRepository.findByAgeBetween(anyInt(), anyInt())).thenReturn(users);
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<UserResponse> result = userService.getUsersByAgeRange(25, 35);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByAgeBetween(25, 35);
    }

    @Test
    void getUsersByIncomeRange_ShouldReturnUsersInRange() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser);
        when(userRepository.findByMonthlyIncomeBetween(anyDouble(), anyDouble())).thenReturn(users);
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<UserResponse> result = userService.getUsersByIncomeRange(4000.0, 6000.0);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByMonthlyIncomeBetween(4000.0, 6000.0);
    }

    @Test
    void getUsersByMinCreditScore_ShouldReturnQualifiedUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser);
        when(userRepository.findByCreditScoreGreaterThanEqual(anyInt())).thenReturn(users);
        when(userMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse, testResponse));

        // Act
        List<UserResponse> result = userService.getUsersByMinCreditScore(700);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findByCreditScoreGreaterThanEqual(700);
    }
}