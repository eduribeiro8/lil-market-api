package com.eduribeiro8.LilMarket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

        userDetailsManager.setUsersByUsernameQuery(
                "select user_name, password, active from users where user_name=?"
        );

        userDetailsManager.setAuthoritiesByUsernameQuery(
                "select user_name, role from roles where user_name=?"
        );

        return userDetailsManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                .requestMatchers(
                        requisitionsAvailableToUsers().concat(HttpMethod.DELETE.toString()),
                        "product/**"
                ).hasAnyRole("USER", "ADMIN")
                .requestMatchers(
                        requisitionsAvailableToUsers().concat(HttpMethod.DELETE.toString()),
                        "sale/**"
                ).hasAnyRole("USER", "ADMIN")
                .requestMatchers(requisitionsAvailableToUsers(), "customer/**").hasRole("USER")
                .anyRequest().hasRole("ADMIN") // ADMIN pode acessar qualquer coisa
        );

        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

//    @Bean
//    public JwtAuthenticationFilter


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String requisitionsAvailableToUsers() {
        return Arrays.toString(new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT});
    }
}
