package org.system.bank.dto.response;

import lombok.*;
import org.system.bank.enums.OtpPurpose;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequiredResponse {
    private String message;
    private String otpRequestEndpoint;
    private OtpPurpose purpose;
}
