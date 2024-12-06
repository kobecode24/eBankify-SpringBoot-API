package com.example.demo.dto.request;

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
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Initial deposit amount is required")
    @Positive(message = "Initial deposit must be greater than zero")
    private Double initialDeposit;
}
