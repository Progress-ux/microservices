package com.progress.gatewayservice.security.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {
   
   @Bean
   public RouteLocator routeLocator(RouteLocatorBuilder builder) {
      return builder.routes().route("auth-service-api", r -> r
         .path("/api/v1/auth/**")
         .uri("http://auth-service:8081")
      ).build();
   }
}
