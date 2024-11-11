package org.system.bank.dto.request;

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
public class AccountCreationRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Positive(message = "Initial deposit must be greater than 0")
    private Double initialDeposit;
}
