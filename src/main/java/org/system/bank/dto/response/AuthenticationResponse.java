package org.system.bank.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.system.bank.enums.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private Long userId;
    private String email;
    private String name;
    private Role role;
    private String token;
    private String refreshToken;
}
