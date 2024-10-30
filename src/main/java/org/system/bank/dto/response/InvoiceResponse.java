package org.system.bank.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.InvoiceStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long invoiceId;
    private Double amountDue;
    private LocalDate dueDate;
    private InvoiceStatus status;
    private Long userId;
    private String userName;
}
