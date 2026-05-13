package com.progress.schoolmicroservices.controller;

import com.progress.schoolmicroservices.model.dto.LoginRequest;
import com.progress.schoolmicroservices.model.dto.LoginResponse;
import com.progress.schoolmicroservices.model.dto.MessageResponse;
import com.progress.schoolmicroservices.model.dto.PublicKeyResponse;
import com.progress.schoolmicroservices.model.dto.RefreshRequest;
import com.progress.schoolmicroservices.model.dto.RefreshResponse;
import com.progress.schoolmicroservices.model.dto.RegisterRequest;
import com.progress.schoolmicroservices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        authService.register(request);
        return ResponseEntity
        .status(HttpStatus.CREATED) 
        .body(new MessageResponse("Пользователь зарегистрирован"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        List<String> tokens = authService.login(request);
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(new LoginResponse(tokens.getFirst(), tokens.getLast()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
        @Valid @RequestBody RefreshRequest request
    ) {
        List<String> tokens = authService.refresh(request.refreshToken());
        return ResponseEntity
            .status(HttpStatus.ACCEPTED)
            .body(new RefreshResponse(tokens.getFirst(), tokens.getLast()));
    }

    @GetMapping("/public-key")
    public ResponseEntity<PublicKeyResponse> getPublicKey() {
        String encodedKey = authService.getEncodedKey();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new PublicKeyResponse(encodedKey));
    }
}
