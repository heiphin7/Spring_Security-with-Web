package com.website.blogs.controllers;

import com.website.blogs.dtos.LoginUserDTO;
import com.website.blogs.entity.User;
import com.website.blogs.services.UserService;
import com.website.blogs.utils.JwtTokenUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final Validator validator;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    @GetMapping("/login")
    public String loginPage(Model model, LoginUserDTO loginUserDTO){
        model.addAttribute("loginUserDTO", loginUserDTO);
        return "loginpage";
    }

    @PostMapping("/login")
    public String checkUser(@Valid LoginUserDTO loginUserDTO, Errors errors, RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            return "loginpage";
        }
/*
        if(!userService.userExists(loginUserDTO.getUsername())){
            errors.rejectValue("username", "erorr.UserExists", "Пользователь не найден");
            return "loginpage";
        }
*/
        //Optional<User> user = userService.findByUsername(loginUserDTO.getUsername());
        //String encodedPassword = passwordEncoder.encode(loginUserDTO.getPassword());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUserDTO.getUsername(), loginUserDTO.getPassword()));
        }catch (BadCredentialsException e){
            errors.rejectValue("username", "erorr.UserExists", "иди нахуй");
        }

        UserDetails user = userService.loadUserByUsername(loginUserDTO.getUsername());
        String token = jwtTokenUtils.generateToken(user);
        return "redirect:/main";
    }
}
