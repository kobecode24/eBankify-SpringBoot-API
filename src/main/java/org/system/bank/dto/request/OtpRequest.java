package org.system.bank.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.system.bank.enums.OtpPurpose;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {
    @NotNull(message = "OTP purpose is required")
    private OtpPurpose purpose;

    @NotNull(message = "Operation ID is required")
    private String operationId;

    @JsonIgnore
    private Long userId;
}
