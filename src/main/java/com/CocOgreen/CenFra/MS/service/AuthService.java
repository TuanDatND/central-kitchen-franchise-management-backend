package com.CocOgreen.CenFra.MS.service;

import com.CocOgreen.CenFra.MS.dto.LoginRequest;
import com.CocOgreen.CenFra.MS.dto.LoginResponse;
import com.CocOgreen.CenFra.MS.entity.User;
import com.CocOgreen.CenFra.MS.security.CustomUserDetails;
import com.CocOgreen.CenFra.MS.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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

        String token = jwtProvider.generateToken(user);

        return new LoginResponse(
                token,
                user.getUserName(),
                user.getRole().getRoleName().name()
        );
    }
}

