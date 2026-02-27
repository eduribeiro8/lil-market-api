package com.eduribeiro8.LilMarket.security;

import com.eduribeiro8.LilMarket.repository.UserRepository;
import com.eduribeiro8.LilMarket.rest.exception.UserNotFoundException;
import com.eduribeiro8.LilMarket.security.logging.LoggingFilter;
import com.eduribeiro8.LilMarket.security.logging.LoggingPreAuthFilter;
import com.eduribeiro8.LilMarket.security.ratelimiting.RateLimitingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final RateLimitingFilter rateLimitingFilter;
    private final LoggingFilter loggingFilter = new LoggingFilter();
    private final LoggingPreAuthFilter loggingPreAuthFilter = new LoggingPreAuthFilter();
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationConfig applicationConfig;
    private final AuthenticationProvider authenticationProvider;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitingFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(loggingPreAuthFilter, AuthorizationFilter.class)
                .addFilterAfter(loggingFilter, AuthorizationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // 1. Define endpoints para o Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // 2. Permite o endpoint login para todos
                        .requestMatchers("/auth/login").permitAll()

                        // 3. Permite depósito apenas para ‘MANAGER’ ou ADMIN
                        .requestMatchers(HttpMethod.POST, "/customer/*/deposit").hasAnyRole("MANAGER", "ADMIN")

                        // 4. Define o que cada role pode fazer
                        .requestMatchers(HttpMethod.GET,
                                "/product/**",
                                "/sale/**", "/customer/**",
                                "/batch/**", "/category/**",
                                "/supplier/**", "/restock/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/product/**",
                                "/sale/**", "/customer/**",
                                "/batch/**", "/category/**",
                                "/supplier/**", "/restock/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/product/**",
                                "/sale/**", "/customer/**",
                                "/batch/**", "/category/**",
                                "/supplier/**", "/restock/**")
                        .hasAnyRole("USER", "MANAGER", "ADMIN")

                        // 5. Define o que é EXCLUSIVO do ADMIN
                        .requestMatchers("/user/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // 6. Qualquer outra coisa que sobrar, exige ADMIN
                        .anyRequest().hasRole("ADMIN")
                );

        return http.build();
    }

}
