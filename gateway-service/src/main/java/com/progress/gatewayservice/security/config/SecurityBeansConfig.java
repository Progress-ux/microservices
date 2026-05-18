package com.progress.gatewayservice.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityBeansConfig {
   @Bean
   public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
      return http
         .csrf(ServerHttpSecurity.CsrfSpec::disable)

         .authorizeExchange(exchanges -> exchanges
            .pathMatchers("/api/v1/auth/**").permitAll()
            .anyExchange().authenticated()
         )
         .oauth2ResourceServer(oauth2 ->oauth2.jwt(Customizer.withDefaults()))
         .build();
   }
}
