package com.progress.authservice.security.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class JwtKeyConfig {
   @Value("${jwt.private-key-path}")
   private Resource privateKeyResource;

   @Value("${jwt.public-key-path}")
   private Resource publicKeyResource;

   @Bean
   public PrivateKey privateKey() throws Exception {
      String privateKeyPEM = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
         .replace("-----BEGIN PRIVATE KEY-----", "")
         .replace("-----END PRIVATE KEY-----", "")
         .replaceAll("\\s", "");

      byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
      KeyFactory kf = KeyFactory.getInstance("RSA");

      return kf.generatePrivate(spec);
   }

   @Bean
   public RSAPublicKey publicKey() throws Exception {
      String publicKeyPEM = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
         .replace("-----BEGIN PUBLIC KEY-----", "")
         .replace("-----END PUBLIC KEY-----", "")
         .replaceAll("\\s", "");


      byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded); 
      KeyFactory kf = KeyFactory.getInstance("RSA");

      return (RSAPublicKey) kf.generatePublic(spec);
   }
}
