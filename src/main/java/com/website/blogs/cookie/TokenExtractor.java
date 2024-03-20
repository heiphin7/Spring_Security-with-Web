package com.website.blogs.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor {
    public String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) { // Проверяем имя cookie
                    return cookie.getValue(); // Возвращаем значение токена
                }
            }
        }
        return null; // Если cookie с именем "jwtToken" не найдено
    }
    // Метод для удаления токена из запроса
    public void removeTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    cookie.setMaxAge(0); // Устанавливаем время жизни cookie в 0, чтобы он удалился
                }
            }
        }
    }
}
