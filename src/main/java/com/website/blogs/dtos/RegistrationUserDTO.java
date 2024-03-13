package com.website.blogs.dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationUserDTO {
    @NotNull(message = "Не должны быть пустым")
    private String username;
    @NotNull(message = "Не должны быть пустым")
    private String password;
    @NotNull(message = "Не должны быть пустым")
    private String confirmPassword;
    @NotNull(message = "Не должны быть пустым")
    @Email(message = "Введите корректный email")
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

