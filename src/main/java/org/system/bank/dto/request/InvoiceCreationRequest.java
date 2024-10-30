package org.system.bank.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreationRequest {
    @NotNull
    @Positive
    private Double amountDue;

    @NotNull
    @Future
    private LocalDate dueDate;

    @NotNull
    private Long userId;
}
