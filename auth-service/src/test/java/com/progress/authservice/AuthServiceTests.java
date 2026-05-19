package com.progress.authservice;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.progress.authservice.exception.EmailAlreadyExistsException;
import com.progress.authservice.exception.InvalidCredentialsException;
import com.progress.authservice.model.dto.LoginRequest;
import com.progress.authservice.model.dto.RegisterRequest;
import com.progress.authservice.model.entity.User;
import com.progress.authservice.repository.UserRepository;
import com.progress.authservice.service.AuthService;


@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

   @Mock
   private UserRepository userRepository;

   @InjectMocks
   private AuthService authService;

   @Mock
   private PasswordEncoder passwordEncoder;  

   @Test
   void register_ShouldThrowException_WhenUserExists() {
      // Given
      String email = "test@example.com";
      RegisterRequest request = new RegisterRequest();
      request.setEmail(email);

      when(userRepository.existsByEmail(email)).thenReturn(true);
      assertThatThrownBy(() -> authService.register(request))
         .isInstanceOf(EmailAlreadyExistsException.class)
         .hasMessageContaining("Пользователь с таким email уже существует");
   }

   @Test
   void login_ShouldThrow_WhenUserNotFound() {
      // Given
      String email = "test@example.com";
      String password = "wrongpassword";

      LoginRequest request = new LoginRequest();
      request.setEmail(email);
      request.setPassword(password);

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
      assertThatThrownBy(() -> authService.login(request))
         .isInstanceOf(InvalidCredentialsException.class)
         .hasMessageContaining("Неверный email или пароль");
   }

   @Test
   void login_ShouldThrow_WhenPasswordIncorrect() {
      // Given
      String email = "test@example.com";

      LoginRequest request = new LoginRequest();
      request.setEmail(email);
      request.setPassword("wrongpassword");

      User user = new User();
      user.setEmail(email);
      user.setPassword("hashed_pass");

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

      when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
      assertThatThrownBy(() -> authService.login(request))
         .isInstanceOf(InvalidCredentialsException.class)
         .hasMessageContaining("Неверный email или пароль");
   }
}