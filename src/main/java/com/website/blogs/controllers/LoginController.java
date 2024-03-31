package com.website.blogs.controllers;

import com.website.blogs.cookie.TokenExtractor;
import com.website.blogs.dtos.LoginUserDTO;
import com.website.blogs.services.UserService;
import com.website.blogs.utils.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private final TokenExtractor tokenExtractor;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @GetMapping("/login")
    public String loginPage(Model model, LoginUserDTO loginUserDTO, HttpServletResponse response, HttpServletRequest request){
        tokenExtractor.removeTokenFromRequest(request);
        model.addAttribute("loginUserDTO", loginUserDTO);
        return "loginpage";
    }

    @PostMapping("/login")
    public Object checkUser(@Valid LoginUserDTO loginUserDTO, Errors errors, RedirectAttributes redirectAttributes, Model model, HttpServletResponse response) {
        if (errors.hasErrors()) {
            return "loginpage";
        }

        try {
            UserDetails userDetails = userService.loadUserByUsername(loginUserDTO.getUsername());

            // Аутентификация пользователя
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword(), userDetails.getAuthorities())
            );

            // Установка аутентифицированного пользователя в контекст безопасности
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenUtils.generateToken(userDetails);

            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setPath("/blogs/");
            cookie.setMaxAge(86400); // 24 hours
            cookie.setHttpOnly(true);

            response.addCookie(cookie);

            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            return "redirect:/blogs/main";

        } catch (BadCredentialsException ex) {
            logger.error("Ошибка аутентификации", ex);
            errors.rejectValue("username", "authenticationError", "Неверное имя пользователя или пароль");
            return "loginpage";
        }
    }

}
