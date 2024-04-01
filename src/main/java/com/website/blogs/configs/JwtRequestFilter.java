package com.website.blogs.configs;

import com.website.blogs.controllers.RegistrationController;
import com.website.blogs.cookie.TokenExtractor;
import com.website.blogs.services.UserService;
import com.website.blogs.utils.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final TokenExtractor tokenExtractor;

    // Игнорируемые страницы, такие как страницы регистрации и авторизации а также статические ресурсы для стилей
    private final List<String> ignoredUrls = Arrays.asList("/login", "/register", "/images/", "/css/", "/error/401", "/error/404");

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (shouldIgnoreRequest(requestURI)) {
            logger.info("Пользователь попал на игнорируемую страницу");
            chain.doFilter(request, response);
            return;
        }

        // Достаём токен, далее нужно проверить, действительный ли токен
        String token = tokenExtractor.extractToken(request);

        if (jwtTokenUtils.validateToken(token)) {
            logger.info("Токен прошёл проверку");
            logger.info("Действующий токен: " + token);
            String username = jwtTokenUtils.getUsername(token);

            if (username != null) {
                logger.info("С токеном все ок!");
                UserDetails user = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authorization = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authorization);
                logger.info("Успешно положили в контекст");
            }
        } else {
            // Удаляем токен из cookie
            tokenExtractor.removeTokenFromRequest(request);
            logger.info("Ошибка: С токеном что-то не так");

            // Ставим статус ответа на 401, то есть unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect("/error/401");
            return;
        }

        // Прогоняем дальше фильтры
        chain.doFilter(request, response);
    }

    private boolean shouldIgnoreRequest(String requestURI) {
        return ignoredUrls.stream().anyMatch(requestURI::startsWith);
    }
}
