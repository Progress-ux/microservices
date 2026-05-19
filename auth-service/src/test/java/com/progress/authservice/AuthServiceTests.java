package com.progress.authservice;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.progress.authservice.exception.EmailAlreadyExistsException;
import com.progress.authservice.exception.InvalidCredentialsException;
import com.progress.authservice.model.dto.LoginRequest;
import com.progress.authservice.model.dto.RefreshResponse;
import com.progress.authservice.model.dto.RegisterRequest;
import com.progress.authservice.model.entity.User;
import com.progress.authservice.repository.UserRepository;
import com.progress.authservice.security.jwt.JwtTokenService;
import com.progress.authservice.service.AuthService;

import io.jsonwebtoken.Claims;


@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

   @Mock
   private UserRepository userRepository;

   @InjectMocks
   private AuthService authService;

   @Mock
   private PasswordEncoder passwordEncoder;  

   @Mock
   private JwtTokenService jwtTokenService;

   @BeforeEach
   void setUp() {
      ReflectionTestUtils.setField(authService, "accessTokenLifeTime", Duration.ofMinutes(15));
      ReflectionTestUtils.setField(authService, "refreshTokenLifeTime", Duration.ofDays(30));
   }

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

   @Test
   void refresh_ShouldReturnNewTokens_WhenRefreshTokenIsValid() {
      String oldRefreshToken = "valid_refresh_token";
      Claims claims = mock(Claims.class);
      
      when(jwtTokenService.parseToken(oldRefreshToken)).thenReturn(claims);
      when(claims.get("token_type", String.class)).thenReturn("REFRESH");
      when(claims.getSubject()).thenReturn("test@example.com");

      when(jwtTokenService.generateToken(anyString(), anyLong(), eq("REFRESH"))).thenReturn("new_refresh_token");
      when(jwtTokenService.generateToken(anyString(), anyLong(), eq("ACCESS"))).thenReturn("new_access_token");

      RefreshResponse response = authService.refresh(oldRefreshToken);

      assertThat(response.refreshToken()).isEqualTo("new_refresh_token");
      assertThat(response.accessToken()).isEqualTo("new_access_token");
   }

   @Test
   void refresh_ShouldThrow_WhenTokenInvalidOrExpired() {
      // Given
      String invalidToken = "invalid_token";

      when(jwtTokenService.parseToken(invalidToken)).thenThrow(new RuntimeException("Expired"));

      assertThatThrownBy(() -> authService.refresh(invalidToken))
         .isInstanceOf(InvalidCredentialsException.class)
         .hasMessageContaining("Токен обновления недействителен или просрочен");
   }
}