package org.system.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.system.bank.dto.response.OtpRequiredResponse;
import org.system.bank.enums.OtpPurpose;
import org.system.bank.exception.GlobalExceptionHandler;
import org.system.bank.exception.InvalidOtpException;
import org.system.bank.exception.OtpRequiredException;
import org.system.bank.service.OtpService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OtpValidationFilter extends OncePerRequestFilter {
    private final OtpService otpService;

    private static final List<RequestMatcher> PROTECTED_URLS = Arrays.asList(
            new AntPathRequestMatcher("/users/**", HttpMethod.DELETE.name()),
            new AntPathRequestMatcher("/transactions", HttpMethod.POST.name()),
            new AntPathRequestMatcher("/loans/*/approve", HttpMethod.POST.name())
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PROTECTED_URLS.stream()
                .noneMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String otpToken = request.getHeader("X-OTP-Token");
            if (otpToken == null) {
                throw new OtpRequiredException(OtpRequiredResponse.builder()
                        .message("OTP required for this operation")
                        .otpRequestEndpoint("/api/otp/generate")
                        .purpose(determineOtpPurpose(request))
                        .build());
            }

            SecurityUser user = (SecurityUser) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

            if (!otpService.validateOtp(user.user(), otpToken, determineOtpPurpose(request))) {
                throw new InvalidOtpException("Invalid OTP token");
            }

            filterChain.doFilter(request, response);
        } catch (OtpRequiredException | InvalidOtpException e) {
            handleOtpException(response, e);
        }
    }

    private OtpPurpose determineOtpPurpose(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (method.equals("DELETE") && path.startsWith("/users/")) {
            return OtpPurpose.DELETE_USER;
        } else if (method.equals("POST") && path.equals("/transactions")) {
            return OtpPurpose.HIGH_VALUE_TRANSACTION;
        } else if (method.equals("POST") && path.matches("/loans/.*/approve")) {
            return OtpPurpose.LOAN_APPROVAL;
        }

        return OtpPurpose.UNKNOWN;
    }

    private void handleOtpException(HttpServletResponse response, Exception ex)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ObjectMapper mapper = new ObjectMapper();
        if (ex instanceof OtpRequiredException) {
            mapper.writeValue(response.getWriter(),
                    ((OtpRequiredException) ex).getOtpRequiredResponse());
        } else {
            mapper.writeValue(response.getWriter(),
                    new GlobalExceptionHandler.ErrorResponse(ex.getMessage()));
        }
    }
}
