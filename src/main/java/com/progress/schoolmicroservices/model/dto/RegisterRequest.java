package com.progress.schoolmicroservices.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email()
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 64, message = "Минимум 8 символов")
    private String password;

    @NotBlank(message = "Поле 'Имя' не может быть пустым")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Поле 'Фамилия' не может быть пустым")
    private String lastName;
}