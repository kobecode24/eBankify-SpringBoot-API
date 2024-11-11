package org.system.bank.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.system.bank.repository.base.BaseRepositoryTest;
import org.system.bank.entity.Invoice;
import org.system.bank.entity.User;
import org.system.bank.enums.InvoiceStatus;
import org.system.bank.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        invoiceRepository.deleteAll();
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
        testUser = userRepository.save(testUser);

        testInvoice = Invoice.builder()
                .amountDue(100.0)
                .dueDate(LocalDate.now().plusDays(7))
                .status(InvoiceStatus.PENDING)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findByUser_ShouldReturnInvoices() {
        // Arrange
        invoiceRepository.save(testInvoice);

        // Act
        List<Invoice> invoices = invoiceRepository.findByUser(testUser);

        // Assert
        assertEquals(1, invoices.size());
        assertEquals(testInvoice.getAmountDue(), invoices.get(0).getAmountDue());
    }

    @Test
    void findByStatus_ShouldReturnInvoices() {
        // Arrange
        invoiceRepository.save(testInvoice);

        // Act
        List<Invoice> pendingInvoices = invoiceRepository.findByStatus(InvoiceStatus.PENDING);
        List<Invoice> paidInvoices = invoiceRepository.findByStatus(InvoiceStatus.PAID);

        // Assert
        assertEquals(1, pendingInvoices.size());
        assertTrue(paidInvoices.isEmpty());
    }

    @Test
    void findByDueDateBefore_ShouldReturnOverdueInvoices() {
        // Arrange
        testInvoice.setDueDate(LocalDate.now().minusDays(1));
        invoiceRepository.save(testInvoice);

        // Act
        List<Invoice> overdueInvoices = invoiceRepository.findByDueDateBefore(LocalDate.now());

        // Assert
        assertEquals(1, overdueInvoices.size());
    }

    @Test
    void findOverdueInvoices_ShouldReturnOverdueInvoices() {
        // Arrange
        testInvoice.setDueDate(LocalDate.now().minusDays(1));
        invoiceRepository.save(testInvoice);

        // Act
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices();

        // Assert
        assertEquals(1, overdueInvoices.size());
    }

    @Test
    void calculateTotalPendingAmount_ShouldReturnCorrectAmount() {
        // Arrange
        invoiceRepository.save(testInvoice);

        Invoice anotherInvoice = Invoice.builder()
                .amountDue(200.0)
                .dueDate(LocalDate.now().plusDays(7))
                .status(InvoiceStatus.PENDING)
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        invoiceRepository.save(anotherInvoice);

        // Act
        Double totalPending = invoiceRepository.calculateTotalPendingAmount(testUser);

        // Assert
        assertEquals(300.0, totalPending);
    }

    @Test
    void hasOverdueInvoices_ShouldReturnTrue_WhenUserHasOverdueInvoices() {
        // Arrange
        testInvoice.setDueDate(LocalDate.now().minusDays(1));
        testInvoice.setStatus(InvoiceStatus.OVERDUE);
        invoiceRepository.save(testInvoice);

        // Act
        boolean hasOverdue = invoiceRepository.hasOverdueInvoices(testUser);

        // Assert
        assertTrue(hasOverdue);
    }
}
