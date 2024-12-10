package org.system.bank.otp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.system.bank.dto.response.OtpResponse;
import org.system.bank.entity.OtpToken;
import org.system.bank.entity.User;
import org.system.bank.enums.OtpPurpose;
import org.system.bank.repository.jpa.OtpTokenRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final OtpTokenRepository otpTokenRepository;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;

    public OtpResponse generateOtp(User user, OtpPurpose purpose) {
        String otp = RandomStringUtils.randomNumeric(OTP_LENGTH);

        OtpToken otpToken = OtpToken.builder()
                .token(otp)
                .user(user)
                .purpose(purpose)
                .expiryTime(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES))
                .used(false)
                .build();

        otpTokenRepository.save(otpToken);

        return OtpResponse.builder()
                .token(otp)
                .validityInMinutes(OTP_VALIDITY_MINUTES)
                .expiryTime(otpToken.getExpiryTime())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public boolean validateOtp(User user, String otp, OtpPurpose purpose) {
        return otpTokenRepository.findValidTokenForVerification(
                        user.getUserId(),
                        purpose,
                        otp,
                        LocalDateTime.now()
                )
                .map(token -> {
                    token.setUsed(true);
                    otpTokenRepository.save(token);
                    return true;
                })
                .orElse(false);
    }
}
