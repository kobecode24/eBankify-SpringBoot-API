package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.system.bank.config.SecurityUser;
import org.system.bank.dto.request.OtpRequest;
import org.system.bank.dto.response.OtpResponse;
import org.system.bank.service.OtpService;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {
    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> requestOtp(@RequestBody OtpRequest request) {
        SecurityUser user = (SecurityUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        OtpResponse response = otpService.generateOtp(
                user.user(),
                request.getPurpose()
        );

        return ResponseEntity.ok(response);
    }
}
