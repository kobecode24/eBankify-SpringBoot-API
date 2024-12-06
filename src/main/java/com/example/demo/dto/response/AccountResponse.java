package com.example.demo.dto.response;

import com.example.demo.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private Double balance;
    private AccountStatus status;
    private Long userId;
    private String userName;
}
