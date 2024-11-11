package org.system.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.system.bank.controller.base.BaseControllerTest;
import org.system.bank.dto.request.LoanApplicationRequest;
import org.system.bank.dto.response.LoanResponse;
import org.system.bank.enums.LoanStatus;
import org.system.bank.service.LoanService;
import org.system.bank.util.TestDataBuilder;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest extends BaseControllerTest {

    @MockBean
    private LoanService loanService;

    private LoanApplicationRequest testRequest;
    private LoanResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = TestDataBuilder.createTestLoanApplicationRequest();
        testResponse = TestDataBuilder.createTestLoanResponse();
    }

    @Test
    void applyForLoan_ShouldReturnCreatedLoan() throws Exception {
        when(loanService.createLoan(any(LoanApplicationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(testResponse.getLoanId()))
                .andExpect(jsonPath("$.principal").value(testResponse.getPrincipal()));
    }

    @Test
    void getLoan_ShouldReturnLoan() throws Exception {
        when(loanService.getLoanById(anyLong()))
                .thenReturn(testResponse);

        mockMvc.perform(get("/loans/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(testResponse.getLoanId()));
    }

    @Test
    void updateLoan_ShouldReturnUpdatedLoan() throws Exception {
        when(loanService.updateLoan(anyLong(), any(LoanApplicationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/loans/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(testResponse.getLoanId()));
    }

    @Test
    void getAllLoans_ShouldReturnLoansList() throws Exception {
        List<LoanResponse> loans = Arrays.asList(testResponse, testResponse);
        when(loanService.getAllLoans()).thenReturn(loans);

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getLoansByUser_ShouldReturnLoansList() throws Exception {
        List<LoanResponse> loans = Arrays.asList(testResponse);
        when(loanService.getLoansByUser(anyLong())).thenReturn(loans);

        mockMvc.perform(get("/loans/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getLoansByStatus_ShouldReturnLoansList() throws Exception {
        List<LoanResponse> loans = Arrays.asList(testResponse);
        when(loanService.getLoansByStatus(any(LoanStatus.class))).thenReturn(loans);

        mockMvc.perform(get("/loans/status/{status}", LoanStatus.PENDING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOverdueLoans_ShouldReturnLoansList() throws Exception {
        List<LoanResponse> loans = Arrays.asList(testResponse);
        when(loanService.getOverdueLoans()).thenReturn(loans);

        mockMvc.perform(get("/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void approveLoan_ShouldReturnApprovedLoan() throws Exception {
        testResponse.setStatus(LoanStatus.APPROVED);
        when(loanService.approveLoan(anyLong())).thenReturn(testResponse);

        mockMvc.perform(post("/loans/{id}/approve", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(LoanStatus.APPROVED.name()));
    }

    @Test
    void rejectLoan_ShouldReturnRejectedLoan() throws Exception {
        testResponse.setStatus(LoanStatus.REJECTED);
        when(loanService.rejectLoan(anyLong())).thenReturn(testResponse);

        mockMvc.perform(post("/loans/{id}/reject", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(LoanStatus.REJECTED.name()));
    }

    @Test
    void processLoanPayment_ShouldReturnUpdatedLoan() throws Exception {
        when(loanService.processLoanPayment(anyLong(), any(Double.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/loans/{id}/payment", 1L)
                        .param("amount", "500.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(testResponse.getLoanId()));
    }

    @Test
    void calculateMonthlyPayment_ShouldReturnAmount() throws Exception {
        when(loanService.calculateMonthlyPayment(anyLong())).thenReturn(500.0);

        mockMvc.perform(get("/loans/{id}/monthly-payment", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("500.0"));
    }

    @Test
    void calculateTotalDebt_ShouldReturnAmount() throws Exception {
        when(loanService.calculateTotalDebt(anyLong())).thenReturn(15000.0);

        mockMvc.perform(get("/loans/user/{userId}/total-debt", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("15000.0"));
    }

    @Test
    void checkLoanEligibility_ShouldReturnEligibilityStatus() throws Exception {
        when(loanService.isEligibleForLoan(anyLong(), any(Double.class)))
                .thenReturn(true);

        mockMvc.perform(get("/loans/user/{userId}/eligibility", 1L)
                        .param("amount", "10000.0"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void applyForLoan_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        LoanApplicationRequest invalidRequest = new LoanApplicationRequest();

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}