package org.system.bank.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.system.bank.dto.request.InvoiceCreationRequest;
import org.system.bank.dto.response.InvoiceResponse;
import org.system.bank.enums.InvoiceStatus;
import org.system.bank.service.InvoiceService;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for managing banking invoices")
@PreAuthorize("isAuthenticated()")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Operation(summary = "Create new invoice", description = "Creates a new invoice in the system")
    @ApiResponse(responseCode = "200", description = "Invoice created successfully")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @PreAuthorize("@invoiceSecurity.canCreateInvoice(#request.userId)")
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceCreationRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @Operation(summary = "Get invoice details", description = "Retrieves details of a specific invoice")
    @PreAuthorize("@invoiceSecurity.canAccessInvoice(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @Operation(summary = "Update invoice", description = "Updates an existing invoice")
    @PreAuthorize("@invoiceSecurity.canModifyInvoice(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceCreationRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }

    @Operation(summary = "Delete invoice", description = "Deletes an invoice from the system")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all invoices", description = "Retrieves all invoices in the system")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @Operation(summary = "Get user invoices", description = "Retrieves all invoices for a specific user")
    @PreAuthorize("@invoiceSecurity.canAccessUserInvoices(#userId)")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUser(userId));
    }

    @Operation(summary = "Get invoices by status", description = "Retrieves invoices filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    @Operation(summary = "Get invoices by due date", description = "Retrieves invoices due on a specific date")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/due-date")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByDueDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDueDate(dueDate));
    }

    @Operation(summary = "Get overdue invoices", description = "Retrieves all overdue invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @Operation(summary = "Process invoice payment", description = "Processes payment for an invoice")
    @PreAuthorize("@invoiceSecurity.canPayInvoice(#id)")
    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> processInvoicePayment(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.processInvoicePayment(id));
    }

    @Operation(summary = "Get total pending amount", description = "Calculates total pending amount for a user")
    @PreAuthorize("@invoiceSecurity.canAccessUserInvoices(#userId)")
    @GetMapping("/user/{userId}/pending-amount")
    public ResponseEntity<Double> calculateTotalPendingAmount(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.calculateTotalPendingAmount(userId));
    }

    @Operation(summary = "Check overdue invoices", description = "Checks if a user has any overdue invoices")
    @PreAuthorize("@invoiceSecurity.canAccessUserInvoices(#userId)")
    @GetMapping("/user/{userId}/has-overdue")
    public ResponseEntity<Boolean> hasOverdueInvoices(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.hasOverdueInvoices(userId));
    }
}
