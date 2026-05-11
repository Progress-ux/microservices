package com.progress.schoolmicroservices.controller;

import com.progress.schoolmicroservices.model.dto.MessageResponse;
import com.progress.schoolmicroservices.model.dto.RegisterRequest;
import com.progress.schoolmicroservices.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
