package com.progress.schoolmicroservices.service;

import com.progress.schoolmicroservices.exception.EmailAlreadyExistsException;
import com.progress.schoolmicroservices.exception.InvalidCredentialsException;
import com.progress.schoolmicroservices.model.dto.LoginRequest;
import com.progress.schoolmicroservices.model.dto.RegisterRequest;
import com.progress.schoolmicroservices.model.entity.User;
import com.progress.schoolmicroservices.model.enums.Role;
import com.progress.schoolmicroservices.repository.UserRepository;
import com.progress.schoolmicroservices.security.jwt.JwtTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public String login(LoginRequest request) {
        

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
            .orElseThrow(() -> new InvalidCredentialsException("Неверный email или пароль"));

        System.out.println("Email from db: " + user.getEmail());
        System.out.println("Password from db: " + user.getPassword());
        System.out.println("request == user" + passwordEncoder.matches(request.getPassword(), user.getPassword()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Неверный email или пароль");
        }

        return jwtTokenService.generateToken(user.getEmail());
    }
}
