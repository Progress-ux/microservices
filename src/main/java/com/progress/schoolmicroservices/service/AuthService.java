package com.progress.schoolmicroservices.service;

import com.progress.schoolmicroservices.exception.EmailAlreadyExistsException;
import com.progress.schoolmicroservices.exception.InvalidCredentialsException;
import com.progress.schoolmicroservices.model.dto.LoginRequest;
import com.progress.schoolmicroservices.model.dto.RegisterRequest;
import com.progress.schoolmicroservices.model.entity.User;
import com.progress.schoolmicroservices.model.enums.Role;
import com.progress.schoolmicroservices.repository.UserRepository;
import com.progress.schoolmicroservices.security.jwt.JwtTokenService;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

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
    public List<String> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
            .orElseThrow(() -> new InvalidCredentialsException("Неверный email или пароль"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Неверный email или пароль");
        }
        
        long ACCESS_TOKEN_MS = 15 * 60 * 1000;
        long REFRESH_TOKEN_MS = 7 * 24 * 60 * 60 * 1000L;

        String accessToken = jwtTokenService.generateToken(user.getEmail(), ACCESS_TOKEN_MS, "ACCESS");
        String refreshToken = jwtTokenService.generateToken(user.getEmail(), REFRESH_TOKEN_MS, "REFRESH");

        return List.of(accessToken, refreshToken);
    }

    @Transactional
    public List<String> refresh(String refreshToken) {
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

            return List.of(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new InvalidCredentialsException("Токен обновления недействителен или просрочен");
        }
    }

    public String getEncodedKey() {
        return Base64.getEncoder().encodeToString(jwtTokenService.getPublicKey().getEncoded());
    }
}
