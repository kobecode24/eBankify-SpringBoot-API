package org.system.bank.service;

import org.system.bank.dto.request.InvoiceCreationRequest;
import org.system.bank.dto.response.InvoiceResponse;
import org.system.bank.enums.InvoiceStatus;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceCreationRequest request);
    InvoiceResponse getInvoiceById(Long id);
    InvoiceResponse updateInvoice(Long id, InvoiceCreationRequest request);
    void deleteInvoice(Long id);
    List<InvoiceResponse> getAllInvoices();
    List<InvoiceResponse> getInvoicesByUser(Long userId);
    List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status);
    List<InvoiceResponse> getOverdueInvoices();
    Double calculateTotalPendingAmount(Long userId);
    InvoiceResponse processInvoicePayment(Long invoiceId);
    boolean hasOverdueInvoices(Long userId);
    void markInvoicesAsOverdue();
    List<InvoiceResponse> getInvoicesByDueDate(LocalDate dueDate);
}
