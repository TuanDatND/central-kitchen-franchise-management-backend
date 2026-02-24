package com.CocOgreen.CenFra.MS.controller;

import com.CocOgreen.CenFra.MS.dto.LoginRequest;
import com.CocOgreen.CenFra.MS.dto.LoginResponse;
import com.CocOgreen.CenFra.MS.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
             @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}

