package com.website.blogs.controllers;

import com.website.blogs.api.EmailChecker;
import com.website.blogs.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ResetPasswordController {

    private final UserService userService;
    private final EmailChecker emailChecker;

    @GetMapping("/reset-password")
    public String resetPage(Model model) {
        model.addAttribute("password", "");
        return "reset-password";
    }

    // Метод для того, чтобы принять почту пользователя и обработать его
    @PostMapping("/reset-password")
    public String resetPasswordPost(@RequestParam(name = "email") String email,
                                    Model model, RedirectAttributes redirectAttributes) {

        // Проверка на isEmpty и isBlank для того, чтобы пользователь не отправлял пустые даныне
        if(email.isEmpty() || email.isBlank()) {
            model.addAttribute("emailError", "Введите корректную почту!");
            return "reset-password";
        }

        // Первичная проверка через регулярные выражения, чтобы оптимизировать запросы к базе данных
        if(!email.matches("^(.+)@(.+)$")) {
            model.addAttribute("emailError", "Некорректный формат почты!");
            return "reset-password";
        }

        // Метод из userService, на проверку существования почты в базе данных
        if(!userService.emailExists(email)) {
            model.addAttribute("emailError", "Такой почты не существует!");
            return "reset-password";
        }

        // Далее мы должны проверить, подтвердил ли почту, чтобы мы могли прислать ему код для восстановления
        if(!emailChecker.checkEmailInIdentity(email)) {
            model.addAttribute("emailError", "Для получения кода, вы должны подтвердить почту, просто нажав на ссыку");
            return "reset-password";
        }

        // Если все круто, тогда перенаправляем пользователя на страницу успешной авторизации
        return "redirect:/reset-password/sended";
    }

    @GetMapping("/reset-password/sended")
    public String resetSended() {
        return "email-sended";
    }
}
