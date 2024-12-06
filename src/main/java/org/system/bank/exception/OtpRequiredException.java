package org.system.bank.exception;

import lombok.Getter;
import org.system.bank.dto.response.OtpRequiredResponse;

@Getter
public class OtpRequiredException extends RuntimeException {
    private final OtpRequiredResponse otpRequiredResponse;

    public OtpRequiredException(OtpRequiredResponse otpRequiredResponse) {
        super(otpRequiredResponse.getMessage());
        this.otpRequiredResponse = otpRequiredResponse;
    }

}
