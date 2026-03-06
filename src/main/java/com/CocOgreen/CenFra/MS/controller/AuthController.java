package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.LoginRequest;
import com.CocOgreen.CenFra.MS.dto.LoginResponse;
import com.CocOgreen.CenFra.MS.dto.RefreshRequest;
import com.CocOgreen.CenFra.MS.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Dev 1 - Authentication", description = "APIs xác thực tài khoản, đăng nhập, làm mới token và đăng xuất")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Đăng nhập bằng tài khoản hợp lệ và nhận access token cùng refresh token.")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới token", description = "Dùng refresh token hiện tại để nhận access token mới và refresh token mới.")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Đăng xuất", description = "Thu hồi toàn bộ refresh token còn hiệu lực của tài khoản đang đăng nhập.")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.noContent().build();
    }
}
