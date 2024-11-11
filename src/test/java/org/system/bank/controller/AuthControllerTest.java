package org.system.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.system.bank.controller.base.BaseControllerTest;
import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.LoginResponse;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.enums.Role;
import org.system.bank.service.AuthService;
import org.system.bank.util.TestDataBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @MockBean
    private AuthService authService;

    private UserRegistrationRequest registrationRequest;
    private LoginRequest loginRequest;
    private UserResponse userResponse;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        registrationRequest = TestDataBuilder.createTestUserRegistrationRequest();

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Test123!");

        userResponse = TestDataBuilder.createTestUserResponse();
        loginResponse = new LoginResponse(1L, "Test User", "test@example.com", Role.USER);
    }

    @Test
    void register_ShouldReturnCreatedUser() throws Exception {
        when(authService.register(any(UserRegistrationRequest.class)))
                .thenReturn(userResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userResponse.getUserId()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(userResponse.getName()));
    }

    @Test
    void login_ShouldReturnLoginResponse() throws Exception {
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(loginResponse.getUserId()))
                .andExpect(jsonPath("$.email").value(loginResponse.getEmail()))
                .andExpect(jsonPath("$.name").value(loginResponse.getName()));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Create an invalid request (missing required fields)
        UserRegistrationRequest invalidRequest = new UserRegistrationRequest();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        // Create an invalid request (missing required fields)
        LoginRequest invalidRequest = new LoginRequest();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}