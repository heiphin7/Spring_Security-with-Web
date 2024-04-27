package com.website.blogs.controllers;

import com.website.blogs.api.EmailChecker;
import com.website.blogs.entity.User;
import com.website.blogs.repository.UUIDRepository;
import com.website.blogs.services.MailSenderService;
import com.website.blogs.services.UUIDService;
import com.website.blogs.services.UserService;
import javassist.NotFoundException;
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
    private final UUIDService uuidService;
    private final UUIDRepository uuidRepository;
    private final MailSenderService mailSenderService;

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

        User user = userService.findByEmail(email).orElse(null);

        if(user == null) {
            model.addAttribute("emailError", "Пользователь с такой почтой не найден!");
            return "reset-password";
        }

        String uuid = uuidService.generateNewToken();

        uuidService.saveNewToken(user, uuid);

        sendUUIDMessage(uuid, user);

        // Если все круто, тогда перенаправляем пользователя на страницу успешного запроса
        return "redirect:/reset-password/sended";
    }

    // Метод для отправки email для восстановления
    private void sendUUIDMessage(String uuid, User user) {
        String resetLink = "http://16.171.166.173:8008/password/reset/" + uuid;
        String subject = "Восстановление пароля";
        String message = "Здравствуйте, " + user.getUsername() + "!\n\n"
                + "Вы запросили восстановление пароля для вашей учетной записи. "
                + "Чтобы завершить процесс, пожалуйста, перейдите по ссылке ниже:\n\n"
                + resetLink + "\n\n"
                + "Если вы не делали этот запрос, проигнорируйте это сообщение.\n\n"
                + "С уважением,\n"
                + "Команда [название вашего сайта]";

        mailSenderService.sendEmail("shalgynbayramazan@gmail.com", user.getEmail(), subject, message);
    }

    @GetMapping("/reset-password/sended")
    public String resetSended() {
        System.out.println("Дошло до метода resetSende");
        return "email-sended";
    }
}
