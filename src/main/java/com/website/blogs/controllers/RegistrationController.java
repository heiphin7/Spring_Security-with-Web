package com.website.blogs.controllers;

import com.website.blogs.api.EmailChecker;
import com.website.blogs.cookie.TokenExtractor;
import com.website.blogs.dtos.RegistrationUserDTO;
import com.website.blogs.entity.User;
import com.website.blogs.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenExtractor tokenExtractor;
    private final EmailChecker emailChecker;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping("/register")
    public String registrationPage(@ModelAttribute("registrationUserDTO") RegistrationUserDTO registrationUserDTO, HttpServletResponse response, HttpServletRequest request){
        tokenExtractor.removeTokenFromRequest(request);
        return "registration";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid RegistrationUserDTO registrationUserDTO, Errors errors, RedirectAttributes redirectAttributes){

        // Сначала у нас идет проверка на количество символов, а далее уже ручные проверки
        if(errors.hasErrors()){
            return "registration";
        }

        // Проверка, имеется ли пробелы в username
        else if (!registrationUserDTO.getUsername().matches("^[a-zA-Zа-яА-Я0-9]+$")) {
            errors.rejectValue("username", "erorrs.NotAvailableUsername", "В имени пользователя можно только буквы и цифры!");
            return "registration";
        }

        // проверка, не занято ли имя пользователя
        if(userService.userExists(registrationUserDTO.getUsername())){
            errors.rejectValue("username", "erorr.UserExists", "Имя пользователя уже занято!");
            return "registration";
        }

        // Проверка на содержание пробелов в email
        if (!registrationUserDTO.getEmail().matches("^[^\\s]+$")) {
            errors.rejectValue("email", "error.notAvailableEmail", "Почта не должна содержать пробелы!");
            return "registration";
        }

        // Проверка на уникальность email-а
        if(userService.emailExists(registrationUserDTO.getEmail())) {
            errors.rejectValue("email", "error.notAvailableEmail", "Данная почта уже используется!");
            return "registration";
        }

        if(!emailChecker.checkEmail(registrationUserDTO.getEmail())){
            errors.rejectValue("email", "error.notAvailableEmail", "Введите существующую почту");
            return "registration";
        }

        // Присылаем на почту сообщение с подтверждением от amazon
        emailChecker.authenticate(registrationUserDTO.getEmail());

        // Проверка подтерждения пароля на наличие пробелов
        if(!registrationUserDTO.getConfirmPassword().matches("^[^\\s]+$")){
            errors.rejectValue("confirmPassword", "erorr.WrongPassword", "Пароль не должен содержать пробелы!");
            return "registration";
        }

        // Первичная проверка пароля перед сравнением
        if(!registrationUserDTO.getPassword().matches("^[^\\s]+$")){
            errors.rejectValue("password", "erorr.password", "Пароль не должен содержать пробелы!");
            return "registration";
        }

        // Если пароль и его подтверждение не совпадают
        if(!registrationUserDTO.getPassword().equals(registrationUserDTO.getConfirmPassword())){
            errors.rejectValue("confirmPassword", "erorr.wrongPasswords", "Пароли не совпадают!");
            errors.rejectValue("password", "erorr.wrongPasswords", "Пароли не совпадают!");
            return "registration";
        }

        // Если все ок, тогда просто кодируем пароль и создаем User, чтобы далее сохранить его в базе данных
        String encodedPassword = passwordEncoder.encode(registrationUserDTO.getPassword());
        User user = new User(registrationUserDTO.getUsername(),encodedPassword, registrationUserDTO.getEmail());

        // Перед сохранением пользователя перепроверяем условия
        String response = userService.saveUser(user);

        if(response.equals("Все поля должны быть заполнены!")) {
            errors.rejectValue("username", "erorr.NullException", "Все поля должны быть заполнены!");
            return "registration";
        } else if(response.equals("Пользователь с таким именем уже сущесвует!")) {
            errors.rejectValue("username", "erorr.UserExists", "Имя пользователя уже занято!");
            return "registration";
        }

        // Сообщение об успешной регистрации
        redirectAttributes.addFlashAttribute("message", "Отлично! Вы успешно прошли регистрацию, теперь подтвердите почту!");
        logger.info("Зарегестрировался новый пользователь! username:" + user.getUsername() + " id: " + user.getId());
        return "redirect:/login";
    }
}
