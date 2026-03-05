package com.CocOgreen.CenFra.MS.config;

import com.CocOgreen.CenFra.MS.security.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtFilter jwtFilter;

        @Value("${app.cors.allowed-origin-patterns:http://localhost:3000,http://localhost:5173,https://*.onrender.com,https://*.vercel.app}")
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
                                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                                .accessDeniedHandler((request, response, e) ->
                                        response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                        )

                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                        "/auth/login",
                                        "/auth/refresh",
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
}