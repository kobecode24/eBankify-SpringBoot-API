package org.system.bank.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.system.bank.entity.OtpToken;
import org.system.bank.entity.User;
import org.system.bank.enums.OtpPurpose;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByUserAndTokenAndPurposeAndUsedFalse(
            User user,
            String token,
            OtpPurpose purpose
    );

    @Query("SELECT o FROM OtpToken o WHERE " +
            "o.user.id = :userId AND " +
            "o.purpose = :purpose AND " +
            "o.token = :token AND " +
            "o.used = false AND " +
            "o.expiryTime > :currentTime")
    Optional<OtpToken> findValidTokenForVerification(
            @Param("userId") Long userId,
            @Param("purpose") OtpPurpose purpose,
            @Param("token") String token,
            @Param("currentTime") LocalDateTime currentTime
    );

    @Query("SELECT COUNT(o) FROM OtpToken o WHERE " +
            "o.user.id = :userId AND " +
            "o.createdAt > :startTime")
    long countRecentTokens(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime
    );
}