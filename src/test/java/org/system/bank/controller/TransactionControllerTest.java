package org.system.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.system.bank.controller.base.BaseControllerTest;
import org.system.bank.dto.request.TransactionRequest;
import org.system.bank.dto.response.TransactionResponse;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;
import org.system.bank.service.TransactionService;
import org.system.bank.util.TestDataBuilder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest extends BaseControllerTest {

    @MockBean
    private TransactionService transactionService;

    private TransactionRequest testRequest;
    private TransactionResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = TestDataBuilder.createTestTransactionRequest();
        testResponse = TestDataBuilder.createTestTransactionResponse();
    }

    @Test
    void createTransaction_ShouldReturnCreatedTransaction() throws Exception {
        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(testResponse.getTransactionId()))
                .andExpect(jsonPath("$.amount").value(testResponse.getAmount()));
    }

    @Test
    void getTransaction_ShouldReturnTransaction() throws Exception {
        when(transactionService.getTransactionById(anyLong()))
                .thenReturn(testResponse);

        mockMvc.perform(get("/transactions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(testResponse.getTransactionId()));
    }

    @Test
    void getAllTransactions_ShouldReturnTransactionsList() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(testResponse, testResponse);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTransactionsByAccount_ShouldReturnTransactionsList() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(testResponse);
        when(transactionService.getTransactionsByAccount(anyLong())).thenReturn(transactions);

        mockMvc.perform(get("/transactions/account/{accountId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTransactionsByType_ShouldReturnTransactionsList() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(testResponse);
        when(transactionService.getTransactionsByType(any(TransactionType.class)))
                .thenReturn(transactions);

        mockMvc.perform(get("/transactions/type/{type}", TransactionType.STANDARD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTransactionsByStatus_ShouldReturnTransactionsList() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(testResponse);
        when(transactionService.getTransactionsByStatus(any(TransactionStatus.class)))
                .thenReturn(transactions);

        mockMvc.perform(get("/transactions/status/{status}", TransactionStatus.COMPLETED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTransactionHistory_ShouldReturnTransactionsList() throws Exception {
        List<TransactionResponse> transactions = Arrays.asList(testResponse);
        when(transactionService.getAccountTransactionHistory(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(transactions);

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        mockMvc.perform(get("/transactions/history")
                        .param("accountId", "1")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void calculateDailyTransactions_ShouldReturnAmount() throws Exception {
        when(transactionService.calculateDailyTransactions(anyLong(), any(LocalDateTime.class)))
                .thenReturn(1000.0);

        mockMvc.perform(get("/transactions/daily-total")
                        .param("accountId", "1")
                        .param("date", LocalDateTime.now().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    @Test
    void createTransaction_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        TransactionRequest invalidRequest = new TransactionRequest();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}