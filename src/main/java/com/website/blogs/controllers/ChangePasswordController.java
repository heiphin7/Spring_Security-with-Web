package com.website.blogs.controllers;

import com.website.blogs.entity.UUID;
import com.website.blogs.entity.User;
import com.website.blogs.repository.UUIDRepository;
import com.website.blogs.services.UUIDService;
import com.website.blogs.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZonedDateTime;

@Controller
@RequiredArgsConstructor
public class ChangePasswordController {

    private final UUIDService uuidService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UUIDRepository uuidRepository;

    @GetMapping("/password/reset/{uuid}")
    public String resetPassword(@PathVariable(name = "uuid") String uuid, RedirectAttributes redirectAttributes) {

        // Проверяем UUID на существование в базе данных
        UUID token = uuidService.findByUUID(uuid);

        if(token == null) {
            redirectAttributes.addFlashAttribute("emailError", "Ссылка на сброс пароля недействительна");
            return "redirect:/reset-password";
        } else {
            ZonedDateTime currentDate = ZonedDateTime.now();
            ZonedDateTime dateOfExpiration = token.getExpirationDate();

            // проверка, не истек ли срок действия токена
            if(currentDate.isAfter(dateOfExpiration)) {
                redirectAttributes.addFlashAttribute("emailError", "Срок действия ссылки истек, попробуйте заново");
                return "redirect:/reset-password";
            }

            // Проверка, не использована ли данная ссылка
            if(token.is_Activated()) {
                redirectAttributes.addFlashAttribute("emailError", "Данная ссылка уже использована!");
                return "redirect:/reset-password";
            }

            redirectAttributes.addFlashAttribute("uuid", uuid);
            return "redirect:/reset-password/{uuid}";
        }
    }

    @GetMapping("/reset-password/{uuid}")
    public String resetPassword(@PathVariable(name = "uuid") String uuid, Model model) {
        return "password-reset-page";
    }

    @PostMapping("/reset-password/{uuid}")
    public String setNewPassword(@PathVariable(name = "uuid") String uuid,
                                 Model model,
                                 String newPassword,
                                 RedirectAttributes redirectAttributes) {
        if(newPassword.isEmpty() || newPassword.isBlank()) {
            model.addAttribute("passwordError", "Пароль не может быть пустым!");
            return "password-reset-page";
        }

        if(newPassword.length() < 8) {
            model.addAttribute("passwordError", "Пароль должен состоять минимум из 8-символов");
            return "password-reset-page";
        }

        UUID token = uuidService.findByUUID(uuid);

        // Достаем по токену пользователя
        User user = userService.findById(token.getUserId()).orElse(null);

        // Проверяем, все ли в порядке
        if(user == null) {
            redirectAttributes.addFlashAttribute("emailError", "Пользователь по токену не найден!");
            return "redirect:/reset-password";
        }

        // Если нормально, тогда мы должны сменить пароль
        userService.changePassword(user, passwordEncoder.encode(newPassword));

        uuidRepository.delete(token);

        redirectAttributes.addFlashAttribute("message", "Пароль успешно изменён, теперь авторизуйтесь");
        return "redirect:/login";
    }
}
