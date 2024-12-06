package org.system.bank.security.expression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;

@Slf4j
@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurityExpression extends SecurityExpressionRoot {

    public boolean canAccessUserData(Long userId) {
        try {
            User currentUser = getCurrentUser();
            log.debug("Checking access for userId={}, currentUser={}, role={}",
                    userId, currentUser.getUserId(), currentUser.getRole());

            boolean hasAccess = currentUser.getRole() == Role.ADMIN ||
                    currentUser.getRole() == Role.EMPLOYEE ||
                    currentUser.getUserId().equals(userId);

            log.debug("Access decision: {}", hasAccess);
            return hasAccess;
        } catch (Exception e) {
            log.error("Error checking user access", e);
            return false;
        }
    }

    public boolean canModifyUser(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == Role.ADMIN ||
                currentUser.getUserId().equals(userId);
    }
}