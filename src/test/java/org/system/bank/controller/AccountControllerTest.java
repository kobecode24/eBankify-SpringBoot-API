package org.system.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.system.bank.controller.base.BaseControllerTest;
import org.system.bank.dto.request.AccountCreationRequest;
import org.system.bank.dto.response.AccountResponse;
import org.system.bank.enums.AccountStatus;
import org.system.bank.service.AccountService;
import org.system.bank.util.TestDataBuilder;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest extends BaseControllerTest {

    @MockBean
    private AccountService accountService;

    private AccountCreationRequest testRequest;
    private AccountResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = TestDataBuilder.createTestAccountCreationRequest();
        testResponse = TestDataBuilder.createTestAccountResponse();
    }

    @Test
    void createAccount_ShouldReturnCreatedAccount() throws Exception {
        when(accountService.createAccount(any(AccountCreationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(testResponse.getAccountId()))
                .andExpect(jsonPath("$.balance").value(testResponse.getBalance()));
    }

    @Test
    void getAccount_ShouldReturnAccount() throws Exception {
        when(accountService.getAccountById(anyLong()))
                .thenReturn(testResponse);

        mockMvc.perform(get("/accounts/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(testResponse.getAccountId()));
    }

    @Test
    void getAllAccounts_ShouldReturnAccountsList() throws Exception {
        List<AccountResponse> accounts = Arrays.asList(testResponse, testResponse);
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAccountsByUser_ShouldReturnAccountsList() throws Exception {
        List<AccountResponse> accounts = Arrays.asList(testResponse);
        when(accountService.getAccountsByUser(anyLong())).thenReturn(accounts);

        mockMvc.perform(get("/accounts/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateAccountStatus_ShouldReturnUpdatedAccount() throws Exception {
        when(accountService.updateAccountStatus(anyLong(), any(AccountStatus.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/accounts/{id}/status", 1L)
                        .param("status", AccountStatus.ACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(AccountStatus.ACTIVE.name()));
    }

    @Test
    void getTotalBalance_ShouldReturnBalance() throws Exception {
        when(accountService.getTotalBalance(anyLong())).thenReturn(1000.0);

        mockMvc.perform(get("/accounts/user/{userId}/balance", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    @Test
    void getAccountsWithMinBalance_ShouldReturnAccountsList() throws Exception {
        List<AccountResponse> accounts = Arrays.asList(testResponse);
        when(accountService.getAccountsWithMinBalance(any(Double.class))).thenReturn(accounts);

        mockMvc.perform(get("/accounts/min-balance")
                        .param("minBalance", "1000.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void updateAccount_ShouldReturnUpdatedAccount() throws Exception {
        when(accountService.updateAccount(anyLong(), any(AccountCreationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/accounts/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(testResponse.getAccountId()));
    }

    @Test
    void deleteAccount_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/accounts/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountsByStatus_ShouldReturnAccountsList() throws Exception {
        List<AccountResponse> accounts = Arrays.asList(testResponse);
        when(accountService.getAccountsByStatus(any(AccountStatus.class)))
                .thenReturn(accounts);

        mockMvc.perform(get("/accounts/status/{status}", AccountStatus.ACTIVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}