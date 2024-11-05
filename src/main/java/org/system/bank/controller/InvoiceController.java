package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceCreationRequest request) {
        return ResponseEntity.ok(invoiceService.createInvoice(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody InvoiceCreationRequest request) {
        return ResponseEntity.ok(invoiceService.updateInvoice(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.getInvoicesByUser(userId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status));
    }

    @GetMapping("/due-date")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByDueDate(@RequestParam LocalDate dueDate) {
        return ResponseEntity.ok(invoiceService.getInvoicesByDueDate(dueDate));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceResponse>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> processInvoicePayment(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.processInvoicePayment(id));
    }

    @GetMapping("/user/{userId}/pending-amount")
    public ResponseEntity<Double> calculateTotalPendingAmount(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.calculateTotalPendingAmount(userId));
    }

    @GetMapping("/user/{userId}/has-overdue")
    public ResponseEntity<Boolean> hasOverdueInvoices(@PathVariable Long userId) {
        return ResponseEntity.ok(invoiceService.hasOverdueInvoices(userId));
    }
}
