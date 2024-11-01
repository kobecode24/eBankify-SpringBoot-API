package org.system.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.system.bank.enums.Role;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String name;
    private String email;
    private Role role;
}
