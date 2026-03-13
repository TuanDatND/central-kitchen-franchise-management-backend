package com.CocOgreen.CenFra.MS.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.CocOgreen.CenFra.MS.dto.ApiResponse;
import com.CocOgreen.CenFra.MS.security.JwtFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtFilter jwtFilter;

        @Value("${cors_allowed_origin_patterns:http://localhost:3000,http://localhost:5173,https://*.vercel.app,https://*.up.railway.app}")
        private String corsAllowedOriginPatterns;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http
                        .csrf(csrf -> csrf.disable())

                        .cors(cors -> {}) // bật CORS dùng bean bên dưới

                        .sessionManagement(session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )

                        .exceptionHandling(ex -> ex
                                .authenticationEntryPoint((request, response, e) ->
                                        writeErrorResponse(
                                                response,
                                                HttpStatus.UNAUTHORIZED,
                                                "Bạn cần đăng nhập để sử dụng API này"))
                                .accessDeniedHandler((request, response, e) ->
                                        writeErrorResponse(
                                                response,
                                                HttpStatus.FORBIDDEN,
                                                "Bạn không có quyền truy cập API này"))
                        )

                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/auth/login",
                                        "/auth/refresh",
                                        "/api/health",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"
                                ).permitAll()
                                .anyRequest().authenticated()
                        )

                        .addFilterBefore(jwtFilter,
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
        @Bean
        public AuthenticationManager authenticationManager(
                AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                List<String> originPatterns = Arrays.stream(corsAllowedOriginPatterns.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                config.setAllowedOriginPatterns(originPatterns);

                config.setAllowedMethods(List.of(
                        "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
                ));

                config.setAllowedHeaders(List.of("*"));
                config.setExposedHeaders(List.of("Authorization"));

                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source =
                        new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);

                return source;
        }

        private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message)
                throws IOException {
                response.setStatus(status.value());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, Object> error = new LinkedHashMap<>();
                error.put("code", status == HttpStatus.UNAUTHORIZED ? "UNAUTHORIZED" : "FORBIDDEN");
                error.put("details", List.of(message));
                error.put("timestamp", LocalDateTime.now());

                new ObjectMapper()
                        .findAndRegisterModules()
                        .writeValue(response.getWriter(), ApiResponse.error(message, error));
        }
}
