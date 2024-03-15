
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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final TokenExtractor tokenExtractor;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        String token = tokenExtractor.extractToken(request);

        if (token != null) {
            logger.info("Токен на базе");

            System.out.println(token);
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
        }
        chain.doFilter(request, response);
    }
}