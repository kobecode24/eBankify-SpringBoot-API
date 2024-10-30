package org.system.bank.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.system.bank.enums.Role;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private Integer age;
    private Double monthlyIncome;
    private Integer creditScore;
    private Role role;
    private List<AccountResponse> accounts;
}
