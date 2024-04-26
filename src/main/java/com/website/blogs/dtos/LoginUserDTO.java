package com.website.blogs.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserDTO {
    @Size(min = 5,message = "Имя должно быть больше 5-ти символов")
    private String username;
    @Size(min = 8, message = "Пароль должен быть от 8-ми символов")
    private String password;

    @Override
    public String toString() {
        return "LoginUserDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}