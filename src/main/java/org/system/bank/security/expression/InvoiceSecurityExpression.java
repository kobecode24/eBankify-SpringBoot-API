package org.system.bank.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.system.bank.entity.Invoice;
import org.system.bank.service.InvoiceService;

@Component("invoiceSecurity")
@RequiredArgsConstructor
public class InvoiceSecurityExpression extends SecurityExpressionRoot {

    private final InvoiceService invoiceService;

    public boolean canCreateInvoice(Long userId) {
        return isAdmin() || getCurrentUser().getUserId().equals(userId);
    }

    public boolean canAccessInvoice(Long invoiceId) {
        Invoice invoice = invoiceService.getInvoiceEntity(invoiceId);
        return isAdmin() ||
                isEmployee() ||
                invoice.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canModifyInvoice(Long invoiceId) {
        Invoice invoice = invoiceService.getInvoiceEntity(invoiceId);
        return isAdmin() || invoice.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canPayInvoice(Long invoiceId) {
        Invoice invoice = invoiceService.getInvoiceEntity(invoiceId);
        return isAdmin() || invoice.getUser().getUserId().equals(getCurrentUser().getUserId());
    }

    public boolean canAccessUserInvoices(Long userId) {
        return isAdmin() ||
                isEmployee() ||
                getCurrentUser().getUserId().equals(userId);
    }
}