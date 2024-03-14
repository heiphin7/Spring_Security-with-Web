
package com.website.blogs.configs;

import com.website.blogs.controllers.RegistrationController;
import com.website.blogs.services.UserService;
import com.website.blogs.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("Фильтр сработал");
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // Проверяем наличие токена в заголовке запроса
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            logger.info("Токен присутсвует");
            jwtToken = requestTokenHeader.substring(7);

            // Получаем имя пользователя из токена
            username = jwtTokenUtils.getUsername(jwtToken);

            // Если имя пользователя есть и аутентификация еще не прошла
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                logger.info("Токен не пустой");
                // Получаем UserDetails пользователя
                UserDetails userDetails = userService.loadUserByUsername(username);

                // Проверяем токен
                if (username != null) {

                    // Создаем аутентификацию пользователя и устанавливаем его в SecurityContext
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info("Успешно положили токен в контекст");
                }
            }
        }
        // Продолжаем выполнение запроса
        chain.doFilter(request, response);
    }
}



