package org.system.bank.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.LoginResponse;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;
import org.system.bank.exception.AuthenticationException;
import org.system.bank.mapper.UserMapper;
import org.system.bank.repository.UserRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.AuthServiceImpl;
import org.system.bank.util.TestDataBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthServiceTest extends BaseServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserRegistrationRequest registrationRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createTestUser();
        registrationRequest = TestDataBuilder.createTestUserRegistrationRequest();
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Test123!");

        userResponse = UserResponse.builder()
                .userId(1L)
                .name(testUser.getName())
                .email(testUser.getEmail())
                .role(testUser.getRole())
                .build();

        testUser.setPassword(BCrypt.hashpw("Test123!", BCrypt.gensalt()));
    }

    @Test
    void register_ShouldReturnUserResponse_WhenValidRequest() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserRegistrationRequest.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        // Act
        UserResponse result = authService.register(registrationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getName(), result.getName());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> authService.register(registrationRequest));
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenValidCredentials() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act
        LoginResponse result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getUserId(), result.getUserId());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AuthenticationException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_ShouldThrowException_WhenInvalidPassword() {
        // Arrange
        loginRequest.setPassword("WrongPassword123!");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(AuthenticationException.class,
                () -> authService.login(loginRequest));
    }
}