package com.progress.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email()
    @NotBlank(message = "Email не может быть пустым")
    @Size(min = 8, max = 64, message = "Email должен содержать от 8 до 64 символов")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, max = 64, message = "Пароль должен содержать от 8 до 64 символов")
    private String password;

    @NotBlank(message = "Поле 'Имя' не может быть пустым")
    @Size(min = 2, max = 64, message = "Имя должно содержать от 2 до 64 символов")
    private String firstName;

    @Size(min = 2, max = 64, message = "Отчество должно содержать от 2 до 64 символов")
    private String middleName;

    @NotBlank(message = "Поле 'Фамилия' не может быть пустым")
    @Size(min = 2, max = 64, message = "Фамилия должна содержать от 2 до 64 символов")
    private String lastName;
}