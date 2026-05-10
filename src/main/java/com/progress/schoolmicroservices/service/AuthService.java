package com.progress.schoolmicroservices.service;

import com.progress.schoolmicroservices.exception.EmailAlreadyExistsException;
import com.progress.schoolmicroservices.model.dto.RegisterRequest;
import com.progress.schoolmicroservices.model.entity.User;
import com.progress.schoolmicroservices.model.enums.Role;
import com.progress.schoolmicroservices.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
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
}
