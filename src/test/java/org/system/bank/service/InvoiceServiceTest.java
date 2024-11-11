package org.system.bank.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.system.bank.dto.request.InvoiceCreationRequest;
import org.system.bank.dto.response.InvoiceResponse;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.Invoice;
import org.system.bank.entity.User;
import org.system.bank.enums.InvoiceStatus;
import org.system.bank.mapper.InvoiceMapper;
import org.system.bank.repository.InvoiceRepository;
import org.system.bank.service.base.BaseServiceTest;
import org.system.bank.service.impl.InvoiceServiceImpl;
import org.system.bank.util.TestDataBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class InvoiceServiceTest extends BaseServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    private User testUser;
    private Invoice testInvoice;
    private InvoiceCreationRequest testRequest;
    private InvoiceResponse testResponse;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.createTestUser();
        testInvoice = TestDataBuilder.createTestInvoice();
        testRequest = TestDataBuilder.createTestInvoiceCreationRequest();

        testUserResponse = UserResponse.builder()
                .userId(testUser.getUserId())
                .name(testUser.getName())
                .email(testUser.getEmail())
                .build();

        testResponse = InvoiceResponse.builder()
                .invoiceId(testInvoice.getInvoiceId())
                .amountDue(testInvoice.getAmountDue())
                .dueDate(testInvoice.getDueDate())
                .status(testInvoice.getStatus())
                .userId(testUser.getUserId())
                .userName(testUser.getName())
                .build();
    }

    @Test
    void createInvoice_ShouldReturnInvoiceResponse() {
        // Arrange
        when(userService.getUserById(anyLong())).thenReturn(testUserResponse);
        when(invoiceMapper.toEntity(any(InvoiceCreationRequest.class))).thenReturn(testInvoice);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);
        when(invoiceMapper.toResponse(any(Invoice.class))).thenReturn(testResponse);

        // Act
        InvoiceResponse result = invoiceService.createInvoice(testRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testInvoice.getAmountDue(), result.getAmountDue());
        assertEquals(InvoiceStatus.PENDING, result.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void getInvoiceById_ShouldReturnInvoice_WhenInvoiceExists() {
        // Arrange
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(testInvoice));
        when(invoiceMapper.toResponse(any(Invoice.class))).thenReturn(testResponse);

        // Act
        InvoiceResponse result = invoiceService.getInvoiceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testInvoice.getAmountDue(), result.getAmountDue());
        verify(invoiceRepository).findById(1L);
    }

    @Test
    void getInvoiceById_ShouldThrowException_WhenInvoiceNotFound() {
        // Arrange
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> invoiceService.getInvoiceById(1L));
        verify(invoiceRepository).findById(anyLong());
    }

    @Test
    void processInvoicePayment_ShouldUpdateStatus() {
        // Arrange
        testInvoice.setStatus(InvoiceStatus.PENDING);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(testInvoice));

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice savedInvoice = invocation.getArgument(0);
            savedInvoice.setStatus(InvoiceStatus.PAID);
            return savedInvoice;
        });

        when(invoiceMapper.toResponse(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            return InvoiceResponse.builder()
                    .invoiceId(invoice.getInvoiceId())
                    .status(invoice.getStatus())
                    .build();
        });

        // Act
        InvoiceResponse result = invoiceService.processInvoicePayment(1L);

        // Assert
        assertEquals(InvoiceStatus.PAID, result.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void processInvoicePayment_ShouldThrowException_WhenNotPending() {
        // Arrange
        testInvoice.setStatus(InvoiceStatus.PAID);
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(testInvoice));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> invoiceService.processInvoicePayment(1L));
    }

    @Test
    void getOverdueInvoices_ShouldReturnOverdueInvoices() {
        // Arrange
        List<Invoice> overdueInvoices = Arrays.asList(testInvoice);
        when(invoiceRepository.findOverdueInvoices()).thenReturn(overdueInvoices);
        when(invoiceMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse));

        // Act
        List<InvoiceResponse> result = invoiceService.getOverdueInvoices();

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(invoiceRepository).findOverdueInvoices();
    }

    @Test
    void calculateTotalPendingAmount_ShouldReturnCorrectAmount() {
        // Arrange
        when(invoiceRepository.calculateTotalPendingAmount(any(User.class))).thenReturn(1000.0);

        // Act
        Double result = invoiceService.calculateTotalPendingAmount(1L);

        // Assert
        assertEquals(1000.0, result);
        verify(invoiceRepository).calculateTotalPendingAmount(any(User.class));
    }

    @Test
    void hasOverdueInvoices_ShouldReturnTrue_WhenUserHasOverdueInvoices() {
        // Arrange
        when(invoiceRepository.hasOverdueInvoices(any(User.class))).thenReturn(true);

        // Act
        boolean result = invoiceService.hasOverdueInvoices(1L);

        // Assert
        assertTrue(result);
        verify(invoiceRepository).hasOverdueInvoices(any(User.class));
    }

    @Test
    void markInvoicesAsOverdue_ShouldUpdateStatus() {
        // Arrange
        List<Invoice> pendingInvoices = Arrays.asList(testInvoice);
        when(invoiceRepository.findByDueDateBefore(any(LocalDate.class))).thenReturn(pendingInvoices);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(testInvoice);

        // Act
        invoiceService.markInvoicesAsOverdue();

        // Assert
        verify(invoiceRepository, times(pendingInvoices.size())).save(any(Invoice.class));
    }

    @Test
    void getInvoicesByDueDate_ShouldReturnInvoices() {
        // Arrange
        List<Invoice> invoices = Arrays.asList(testInvoice);
        LocalDate dueDate = LocalDate.now();
        when(invoiceRepository.findByDueDateBefore(dueDate)).thenReturn(invoices);
        when(invoiceMapper.toResponseList(anyList())).thenReturn(Arrays.asList(testResponse));

        // Act
        List<InvoiceResponse> result = invoiceService.getInvoicesByDueDate(dueDate);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(invoiceRepository).findByDueDateBefore(dueDate);
    }
}
