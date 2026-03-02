package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.LoginRequest;
import com.CocOgreen.CenFra.MS.dto.LoginResponse;
import com.CocOgreen.CenFra.MS.dto.RefreshRequest;
import com.CocOgreen.CenFra.MS.entity.RefreshToken;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.repository.RefreshTokenRepository;
import com.CocOgreen.CenFra.MS.repository.UserRepository;
import com.CocOgreen.CenFra.MS.security.CustomUserDetails;
import com.CocOgreen.CenFra.MS.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_EXPIRES_LABEL = "7d";

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        String accessToken = jwtProvider.generateToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        persistRefreshToken(user, refreshToken);

        return buildAuthResponse(user, accessToken, refreshToken, "Đăng nhập thành công");
    }

    @Transactional
    public LoginResponse refreshToken(RefreshRequest request) {
        String rawRefreshToken = request.getRefreshToken();
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHashAndRevokedFalseAndExpiresAtAfter(tokenHash, Instant.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        var claims = jwtProvider.parseToken(rawRefreshToken);
        if (!"refresh".equals(claims.get("type"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String username = claims.getSubject();
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is inactive");
        }
        if (!storedToken.getUser().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String newAccessToken = jwtProvider.generateToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);

        storedToken.setRevoked(true);
        storedToken.setReplacedByTokenHash(hashToken(newRefreshToken));
        refreshTokenRepository.save(storedToken);

        persistRefreshToken(user, newRefreshToken);
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());

        return buildAuthResponse(user, newAccessToken, newRefreshToken, "Làm mới token thành công");
    }

    @Transactional
    public void logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken
                || "anonymousUser".equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userRepository.findByUserName(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        refreshTokenRepository.revokeAllActiveByUserId(user.getUserId());
    }

    private LoginResponse buildAuthResponse(User user, String accessToken, String refreshToken, String message) {
        LoginResponse.UserPayload userPayload = new LoginResponse.UserPayload(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                List.of(user.getRole().getRoleName().name())
        );
        LoginResponse.LoginData data = new LoginResponse.LoginData(
                "Bearer " + accessToken,
                refreshToken,
                REFRESH_EXPIRES_LABEL,
                userPayload
        );
        return new LoginResponse(message, data);
    }

    private void persistRefreshToken(User user, String rawRefreshToken) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawRefreshToken));
        refreshToken.setExpiresAt(jwtProvider.parseToken(rawRefreshToken).getExpiration().toInstant());
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", e);
        }
    }
}
