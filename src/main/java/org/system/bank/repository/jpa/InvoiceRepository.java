package org.system.bank.repository.jpa;

import org.system.bank.entity.Invoice;
import org.system.bank.entity.User;
import org.system.bank.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUser(User user);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByDueDateBefore(LocalDate date);

    List<Invoice> findByUserAndStatus(User user, InvoiceStatus status);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < CURRENT_DATE AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices();

    @Query("SELECT SUM(i.amountDue) FROM Invoice i WHERE i.user = :user AND i.status = 'PENDING'")
    Double calculateTotalPendingAmount(@Param("user") User user);

    @Query("SELECT COUNT(i) > 0 FROM Invoice i " +
            "WHERE i.user = :user AND i.status = 'OVERDUE'")
    boolean hasOverdueInvoices(@Param("user") User user);
}
