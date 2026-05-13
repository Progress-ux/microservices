package com.progress.schoolmicroservices.security.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenService {

   private final PrivateKey privateKey;
   private final PublicKey publicKey;

   public JwtTokenService(
      PrivateKey privateKey,
      PublicKey publicKey
   ) {
      this.privateKey = privateKey;
      this.publicKey = publicKey;
   }

   public String generateToken(String email, long duration, String tokenType) {
      return Jwts.builder()
         .subject(email)
         .claim("token_type", tokenType)
         .issuedAt(new Date())
         .expiration(new Date(System.currentTimeMillis() + duration))
         .signWith(privateKey)
         .compact();
   }

   public Claims parseToken(String token) {
      return Jwts.parser()
         .verifyWith(publicKey)
         .build()
         .parseSignedClaims(token)
         .getPayload();
   }

   public PublicKey getPublicKey() { return publicKey; }
}
