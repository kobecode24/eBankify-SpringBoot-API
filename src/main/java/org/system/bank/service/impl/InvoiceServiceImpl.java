package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.dto.request.InvoiceCreationRequest;
import org.system.bank.dto.response.InvoiceResponse;
import org.system.bank.entity.Invoice;
import org.system.bank.entity.User;
import org.system.bank.enums.InvoiceStatus;
import org.system.bank.mapper.InvoiceMapper;
import org.system.bank.repository.jpa.InvoiceRepository;
import org.system.bank.service.InvoiceService;
import org.system.bank.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final UserService userService;

    @Override
    public InvoiceResponse createInvoice(InvoiceCreationRequest request) {
        userService.getUserById(request.getUserId());

        Invoice invoice = invoiceMapper.toEntity(request);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(savedInvoice);
    }

    @Override
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = findInvoiceById(id);
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceResponse updateInvoice(Long id, InvoiceCreationRequest request) {
        findInvoiceById(id);

        Invoice invoiceToUpdate = invoiceMapper.toEntity(request);
        invoiceToUpdate.setInvoiceId(id);

        Invoice updatedInvoice = invoiceRepository.save(invoiceToUpdate);
        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Override
    public void deleteInvoice(Long id) {
        findInvoiceById(id);
        invoiceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceMapper.toResponseList(invoiceRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByUser(Long userId) {
        User user = new User();
        user.setUserId(userId);
        return invoiceMapper.toResponseList(invoiceRepository.findByUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceMapper.toResponseList(invoiceRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getOverdueInvoices() {
        return invoiceMapper.toResponseList(invoiceRepository.findOverdueInvoices());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalPendingAmount(Long userId) {
        User user = new User();
        user.setUserId(userId);
        return invoiceRepository.calculateTotalPendingAmount(user);
    }

    @Override
    public InvoiceResponse processInvoicePayment(Long invoiceId) {
        Invoice invoice = findInvoiceById(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new IllegalStateException("Invoice is not in PENDING state");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(updatedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOverdueInvoices(Long userId) {
        User user = new User();
        user.setUserId(userId);
        return invoiceRepository.hasOverdueInvoices(user);
    }

    @Override
    public void markInvoicesAsOverdue() {
        List<Invoice> pendingInvoices = invoiceRepository.findByDueDateBefore(LocalDate.now());
        for (Invoice invoice : pendingInvoices) {
            if (invoice.getStatus() == InvoiceStatus.PENDING) {
                invoice.setStatus(InvoiceStatus.OVERDUE);
                invoiceRepository.save(invoice);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByDueDate(LocalDate dueDate) {
        List<Invoice> invoices = invoiceRepository.findByDueDateBefore(dueDate);
        return invoiceMapper.toResponseList(invoices);
    }

    private Invoice findInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
    }
}
