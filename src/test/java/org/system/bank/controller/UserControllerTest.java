package org.system.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.system.bank.controller.base.BaseControllerTest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.enums.Role;
import org.system.bank.service.UserService;
import org.system.bank.util.TestDataBuilder;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest extends BaseControllerTest {

    @MockBean
    private UserService userService;

    private UserRegistrationRequest testRequest;
    private UserResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = TestDataBuilder.createTestUserRegistrationRequest();
        testResponse = TestDataBuilder.createTestUserResponse();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(UserRegistrationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testResponse.getUserId()))
                .andExpect(jsonPath("$.email").value(testResponse.getEmail()));
    }

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(testResponse);

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testResponse.getUserId()))
                .andExpect(jsonPath("$.email").value(testResponse.getEmail()));
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        List<UserResponse> users = Arrays.asList(testResponse, testResponse);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        when(userService.updateUser(anyLong(), any(UserRegistrationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(testResponse.getUserId()));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getUsersByRole_ShouldReturnUsersList() throws Exception {
        List<UserResponse> users = Arrays.asList(testResponse);
        when(userService.getUsersByRole(any(Role.class))).thenReturn(users);

        mockMvc.perform(get("/users/role/{role}", Role.USER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUsersByAgeRange_ShouldReturnUsersList() throws Exception {
        List<UserResponse> users = Arrays.asList(testResponse);
        when(userService.getUsersByAgeRange(any(Integer.class), any(Integer.class)))
                .thenReturn(users);

        mockMvc.perform(get("/users/age-range")
                        .param("minAge", "20")
                        .param("maxAge", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUsersByIncomeRange_ShouldReturnUsersList() throws Exception {
        List<UserResponse> users = Arrays.asList(testResponse);
        when(userService.getUsersByIncomeRange(any(Double.class), any(Double.class)))
                .thenReturn(users);

        mockMvc.perform(get("/users/income-range")
                        .param("minIncome", "3000.0")
                        .param("maxIncome", "6000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUsersByMinCreditScore_ShouldReturnUsersList() throws Exception {
        List<UserResponse> users = Arrays.asList(testResponse);
        when(userService.getUsersByMinCreditScore(any(Integer.class)))
                .thenReturn(users);

        mockMvc.perform(get("/users/credit-score")
                        .param("minCreditScore", "700"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void checkLoanEligibility_ShouldReturnEligibilityStatus() throws Exception {
        when(userService.isEligibleForLoan(anyLong())).thenReturn(true);

        mockMvc.perform(get("/users/{id}/loan-eligibility", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}