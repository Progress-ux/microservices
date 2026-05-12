package com.progress.schoolmicroservices.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

   @Email
   @NotBlank(message = "Email не может быть пустым")
   private String email;
   
   @NotBlank(message = "Пароль не может быть пустым")
   @Size(min = 8, max = 64, message = "Минимум 8 символов")
   private String password;
}
