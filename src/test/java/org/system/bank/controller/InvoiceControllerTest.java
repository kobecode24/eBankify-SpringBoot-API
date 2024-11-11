package org.system.bank.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.system.bank.controller.base.BaseControllerTest;
import org.system.bank.dto.request.InvoiceCreationRequest;
import org.system.bank.dto.response.InvoiceResponse;
import org.system.bank.enums.InvoiceStatus;
import org.system.bank.service.InvoiceService;
import org.system.bank.util.TestDataBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest extends BaseControllerTest {

    @MockBean
    private InvoiceService invoiceService;

    private InvoiceCreationRequest testRequest;
    private InvoiceResponse testResponse;

    @BeforeEach
    void setUp() {
        testRequest = TestDataBuilder.createTestInvoiceCreationRequest();
        testResponse = TestDataBuilder.createTestInvoiceResponse();
    }

    @Test
    void createInvoice_ShouldReturnCreatedInvoice() throws Exception {
        when(invoiceService.createInvoice(any(InvoiceCreationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(testResponse.getInvoiceId()))
                .andExpect(jsonPath("$.amountDue").value(testResponse.getAmountDue()));
    }

    @Test
    void getInvoice_ShouldReturnInvoice() throws Exception {
        when(invoiceService.getInvoiceById(anyLong()))
                .thenReturn(testResponse);

        mockMvc.perform(get("/invoices/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(testResponse.getInvoiceId()));
    }

    @Test
    void updateInvoice_ShouldReturnUpdatedInvoice() throws Exception {
        when(invoiceService.updateInvoice(anyLong(), any(InvoiceCreationRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/invoices/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceId").value(testResponse.getInvoiceId()));
    }

    @Test
    void deleteInvoice_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/invoices/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllInvoices_ShouldReturnInvoicesList() throws Exception {
        List<InvoiceResponse> invoices = Arrays.asList(testResponse, testResponse);
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getInvoicesByUser_ShouldReturnInvoicesList() throws Exception {
        List<InvoiceResponse> invoices = Arrays.asList(testResponse);
        when(invoiceService.getInvoicesByUser(anyLong())).thenReturn(invoices);

        mockMvc.perform(get("/invoices/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getInvoicesByStatus_ShouldReturnInvoicesList() throws Exception {
        List<InvoiceResponse> invoices = Arrays.asList(testResponse);
        when(invoiceService.getInvoicesByStatus(any(InvoiceStatus.class))).thenReturn(invoices);

        mockMvc.perform(get("/invoices/status/{status}", InvoiceStatus.PENDING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getInvoicesByDueDate_ShouldReturnInvoicesList() throws Exception {
        List<InvoiceResponse> invoices = Arrays.asList(testResponse);
        when(invoiceService.getInvoicesByDueDate(any(LocalDate.class))).thenReturn(invoices);

        mockMvc.perform(get("/invoices/due-date")
                        .param("dueDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getOverdueInvoices_ShouldReturnInvoicesList() throws Exception {
        List<InvoiceResponse> invoices = Arrays.asList(testResponse);
        when(invoiceService.getOverdueInvoices()).thenReturn(invoices);

        mockMvc.perform(get("/invoices/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void processInvoicePayment_ShouldReturnUpdatedInvoice() throws Exception {
        testResponse.setStatus(InvoiceStatus.PAID);
        when(invoiceService.processInvoicePayment(anyLong())).thenReturn(testResponse);

        mockMvc.perform(post("/invoices/{id}/pay", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(InvoiceStatus.PAID.name()));
    }

    @Test
    void calculateTotalPendingAmount_ShouldReturnAmount() throws Exception {
        when(invoiceService.calculateTotalPendingAmount(anyLong())).thenReturn(1000.0);

        mockMvc.perform(get("/invoices/user/{userId}/pending-amount", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }

    @Test
    void hasOverdueInvoices_ShouldReturnStatus() throws Exception {
        when(invoiceService.hasOverdueInvoices(anyLong())).thenReturn(true);

        mockMvc.perform(get("/invoices/user/{userId}/has-overdue", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void createInvoice_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        InvoiceCreationRequest invalidRequest = new InvoiceCreationRequest();

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}