package com.progress.authservice.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityBeansConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
        "/v3/api-docs/**",
                    "/v3/api-docs.yaml",          
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/swagger-resources/**",       
                    "/webjars/**",                
                    "/api/v1/auth/**",
                    "/error"
                ).permitAll()

                .requestMatchers("/login", "/register", "/refresh", "/.well-known/jwks.json").permitAll()
                .anyRequest().authenticated()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .formLogin(form -> form.disable())
        .httpBasic(basic -> basic.disable());

        return httpSecurity.build();
    }
}
