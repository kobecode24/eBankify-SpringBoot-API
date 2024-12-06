package org.system.bank.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationRequest {
    @NotNull(message = "Principal amount is required")
    @Positive(message = "Principal amount must be positive")
    private Double principal;

    @NotNull(message = "Loan term is required")
    @Min(value = 12, message = "Minimum loan term is 12 months")
    private Integer termMonths;

    @NotNull(message = "User ID is required")
    private Long userId;

    private String guarantees;
}