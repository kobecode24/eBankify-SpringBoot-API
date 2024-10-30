package org.system.bank.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.AccountStatus;

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
