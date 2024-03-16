package com.website.blogs.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationUserDTO {
    @Size(min = 5,message = "Имя должно быть больше 5-ти символов")
    private String username;
    @Size(min=8, message = "Пароль должен быть больше 8-ми символов")
    private String password;
    @Size(min = 8, message = "Подтвердите пароль")
    private String confirmPassword;
    @Size(min = 5, message = "Введите правильную почту")
    private String email;

}

