package org.system.bank.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.TransactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull
    @Positive
    private Double amount;

    @NotNull
    private Long sourceAccountId;

    @NotNull
    private Long destinationAccountId;

    @NotNull
    private TransactionType type;
}
