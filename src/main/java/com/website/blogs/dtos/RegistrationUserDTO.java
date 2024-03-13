package com.website.blogs.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
    @Size(min = 5, message = "Введите корректный email")
    private String email;

    @Override
    public String toString() {
        return "RegistrationUserDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

