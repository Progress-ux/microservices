package com.progress.authservice;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.progress.authservice.exception.EmailAlreadyExistsException;
import com.progress.authservice.model.dto.RegisterRequest;
import com.progress.authservice.repository.UserRepository;
import com.progress.authservice.service.AuthService;


@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

   @Mock
   private UserRepository userRepository;

   @InjectMocks
   private AuthService authService;

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
}