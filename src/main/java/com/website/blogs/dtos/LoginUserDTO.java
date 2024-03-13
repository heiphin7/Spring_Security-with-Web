package com.website.blogs.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserDTO {
    @NotBlank(message = "Имя пользователя не должно быть пустым")
    private String username;
    @NotBlank(message = "Пароль не может быть пустым!")
    private String password;

    @Override
    public String toString() {
        return "LoginUserDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
