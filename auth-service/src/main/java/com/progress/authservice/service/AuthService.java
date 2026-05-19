package com.progress.authservice.service;

import com.progress.authservice.exception.EmailAlreadyExistsException;
import com.progress.authservice.exception.InvalidCredentialsException;
import com.progress.authservice.model.dto.JwkDto;
import com.progress.authservice.model.dto.JwkResponse;
import com.progress.authservice.model.dto.LoginRequest;
import com.progress.authservice.model.dto.LoginResponse;
import com.progress.authservice.model.dto.RefreshResponse;
import com.progress.authservice.model.dto.RegisterRequest;
import com.progress.authservice.model.entity.User;
import com.progress.authservice.model.enums.Role;
import com.progress.authservice.repository.UserRepository;
import com.progress.authservice.security.jwt.JwtTokenService;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService; 

    private final RSAPublicKey publicKey;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new EmailAlreadyExistsException("Пользователь с таким email уже существует");
        }

        User user = new User();

        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setRole(Role.STUDENT);

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
            .orElseThrow(() -> new InvalidCredentialsException("Неверный email или пароль"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Неверный email или пароль");
        }
        
        long ACCESS_TOKEN_MS = 15 * 60 * 1000;
        long REFRESH_TOKEN_MS = 7 * 24 * 60 * 60 * 1000L;

        String accessToken = jwtTokenService.generateToken(user.getEmail(), ACCESS_TOKEN_MS, "ACCESS");
        String refreshToken = jwtTokenService.generateToken(user.getEmail(), REFRESH_TOKEN_MS, "REFRESH");

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public RefreshResponse refresh(String refreshToken) {
        try {
            Claims claims = jwtTokenService.parseToken(refreshToken);

            String tokenType = claims.get("token_type", String.class);
            if (!"REFRESH".equals(tokenType)) {
                throw new InvalidCredentialsException("Неверный тип токена");
            }

            String email = claims.getSubject();

            long ACCESS_TOKEN_MS = 15 * 60 * 1000;
            long REFRESH_TOKEN_MS = 7 * 24 * 60 * 60 * 1000L;

            String newAccessToken = jwtTokenService.generateToken(email, ACCESS_TOKEN_MS, "ACCESS");
            String newRefreshToken = jwtTokenService.generateToken(email, REFRESH_TOKEN_MS, "REFRESH");

            return new RefreshResponse(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Токен обновления недействителен или просрочен");
        }
    }

    public JwkResponse getJwkSet() {
        String modulus = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(publicKey.getModulus().toByteArray());
        String exponent = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(publicKey.getPublicExponent().toByteArray());
        
        JwkDto jwk = new JwkDto(
            "RSA",
            "RS256",
            "sig",
            "school-auth-key-id",
            modulus,
            exponent
        );
        return new JwkResponse(List.of(jwk));
    }
}
