package org.system.bank.dto.response;

import lombok.Data;
import lombok.Builder;
import org.system.bank.enums.TransactionType;
import java.time.LocalDateTime;

@Data
@Builder
public class CartTransactionDTO {
    private String id;
    private Double amount;
    private TransactionType type;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private String description;
}