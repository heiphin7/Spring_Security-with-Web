package com.website.blogs.configs;

import com.website.blogs.controllers.RegistrationController;
import com.website.blogs.cookie.TokenExtractor;
import com.website.blogs.entity.User;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final List<String> ignoredUrls = Arrays.asList
            ("/login", "/register", "/images/", "/css/", "/reset-password/email/sended" ,
                    "/error/401", "/error/404", "/reset-password", "/reset-password/success", "/password/reset/");

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (shouldIgnoreRequest(requestURI) || requestURI.startsWith("/password/reset/") || requestURI.startsWith("/swagger")) {
            System.out.println(requestURI);
            chain.doFilter(request, response);
            return;
        }

        if(requestURI.startsWith("/verification/")) {
            chain.doFilter(request, response);
            return;
        }

        // Достаём токен, далее нужно проверить, действительный ли токен
        String token = tokenExtractor.extractToken(request);

        if (jwtTokenUtils.validateToken(token)) {
            String username = jwtTokenUtils.getUsername(token);
            User userInDB = userService.findByUsername(username).orElse(null);

            if(userInDB == null) {
                // Если пользователь не найден или он не activated
                // Ставим статус ответа на 401, то есть unauthorized

                response.sendRedirect("/error/401");
                return;
            }

            if (username != null) {
                logger.info("С токеном все ок!");
                UserDetails user = userService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authorization = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authorization);
            }
        } else {
            // Удаляем токен из cookie
            tokenExtractor.removeTokenFromRequest(request);

            // Ставим статус ответа на 401, то есть unauthorized
            if(!response.isCommitted()){
                response.sendRedirect("/error/401");
                return;
            }

        }

        // Прогоняем дальше фильтры
        chain.doFilter(request, response);
    }

    private boolean shouldIgnoreRequest(String requestURI) {
        return ignoredUrls.stream().anyMatch(requestURI::startsWith);
    }
}
