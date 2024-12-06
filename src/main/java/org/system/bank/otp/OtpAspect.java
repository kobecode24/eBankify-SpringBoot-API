package org.system.bank.otp;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.*;
import org.system.bank.config.SecurityUser;
import org.system.bank.dto.response.OtpRequiredResponse;
import org.system.bank.exception.InvalidOtpException;
import org.system.bank.exception.OtpRequiredException;
import org.system.bank.service.OtpService;

@Aspect
@Component
@RequiredArgsConstructor
public class OtpAspect {
    private final OtpService otpService;
    private final HttpServletRequest httpServletRequest;

    @Around("@annotation(requiresOtp)")
    public Object validateOtp(ProceedingJoinPoint joinPoint, RequiresOtp requiresOtp)
            throws Throwable {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String otpToken = httpServletRequest.getHeader("X-OTP-Token");
        if (otpToken == null) {
            throw new OtpRequiredException(OtpRequiredResponse.builder()
                    .message("OTP is required for this operation")
                    .otpRequestEndpoint("/api/otp/generate")
                    .purpose(requiresOtp.purpose())
                    .build());
        }

        if (!otpService.validateOtp(user.user(), otpToken, requiresOtp.purpose())) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        return joinPoint.proceed();
    }
}
