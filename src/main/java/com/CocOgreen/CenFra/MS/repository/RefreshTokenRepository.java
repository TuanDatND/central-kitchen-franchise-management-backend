package com.CocOgreen.CenFra.MS.repository;

import com.CocOgreen.CenFra.MS.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashAndRevokedFalseAndExpiresAtAfter(String tokenHash, Instant now);

    int deleteByExpiresAtBefore(Instant now);

    @Modifying
    @Query("""
            update RefreshToken rt
               set rt.revoked = true
             where rt.user.userId = :userId
               and rt.revoked = false
            """)
    int revokeAllActiveByUserId(@Param("userId") Integer userId);
}
