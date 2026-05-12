package com.progress.schoolmicroservices.security.config;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class JwtKeyConfig {
   @Value("${jwt.private-key-path}")
   private Resource keyResource;
   
   @Bean
   public PrivateKey privateKey() throws Exception {
      byte[] keyBytes = keyResource.getInputStream().readAllBytes();
      String tempKey = new String(keyBytes, StandardCharsets.UTF_8);

      String privateKeyPEM = tempKey
      .replace("-----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "")
      .replaceAll("\\s", "");

      byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
      KeyFactory kf = KeyFactory.getInstance("RSA");

      return kf.generatePrivate(spec);
   }

}
