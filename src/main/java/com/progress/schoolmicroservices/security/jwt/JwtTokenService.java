package com.progress.schoolmicroservices.security.jwt;

import java.security.PrivateKey;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenService {

   private final PrivateKey privateKey;

   public JwtTokenService(
      PrivateKey privateKey
   ) {
      this.privateKey = privateKey;
   }

   public String generateToken(String email) {
      return Jwts.builder()
         .subject(email)
         .issuedAt(new Date())
         .expiration(new Date(System.currentTimeMillis() + 1000 * 60))
         .signWith(privateKey)
         .compact();
   }
}
