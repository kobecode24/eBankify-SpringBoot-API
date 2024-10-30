package org.system.bank.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.TransactionStatus;
import org.system.bank.enums.TransactionType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private Double amount;
    private TransactionType type;
    private TransactionStatus status;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private LocalDateTime createdAt;
    private Double fee;
}
