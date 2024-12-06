package org.system.bank.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {
    private String token;
    private int validityInMinutes;
    private LocalDateTime expiryTime;
    private LocalDateTime timestamp;
}