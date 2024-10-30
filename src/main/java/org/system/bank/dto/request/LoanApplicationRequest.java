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
    @NotNull
    @Positive
    private Double principal;

    @NotNull
    @Min(12)
    private Integer termMonths;

    @NotNull
    private Long userId;

    private String guarantees;
}