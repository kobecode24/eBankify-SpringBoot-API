package org.system.bank.security.expression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.system.bank.config.SecurityUser;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;
import org.system.bank.exception.AuthenticationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityExpressionRoot {

    protected User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.error("No authentication found in SecurityContext");
            throw new AuthenticationException("No authentication found");
        }

        Object principal = authentication.getPrincipal();
        log.debug("Current principal type: {}", principal.getClass().getName());

        if (principal instanceof SecurityUser securityUser) {
            User user = securityUser.getUser();
            log.debug("Retrieved user: id={}, role={}", user.getUserId(), user.getRole());
            return user;
        }

        log.error("Unexpected principal type: {}", principal.getClass().getName());
        throw new AuthenticationException("Invalid authentication type");
    }

    protected boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    protected boolean isEmployee() {
        return getCurrentUser().getRole() == Role.EMPLOYEE;
    }
}