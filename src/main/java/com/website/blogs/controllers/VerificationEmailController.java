package com.website.blogs.controllers;

import com.website.blogs.api.EmailChecker;
import com.website.blogs.entity.User;
import com.website.blogs.entity.VerificationCode;
import com.website.blogs.repository.VerificationCodeRepository;
import com.website.blogs.services.MailSenderService;
import com.website.blogs.services.OTPGenerator;
import com.website.blogs.services.UserService;
import com.website.blogs.services.VerificationCodeService;
import com.website.blogs.utils.JwtTokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZonedDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/verification/")
public class VerificationEmailController {
    private final String emailSender = "shalgynbayramazan@gmail.com";

    private final UserService userService;
    private final OTPGenerator otpGenerator;
    private final MailSenderService mailSenderService;
    private final VerificationCodeService verificationCodeService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailChecker emailChecker;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/email-verification/{username}")
    public String emailVerification(@PathVariable(name = "username") String username,
                                    Model model,
                                    HttpServletRequest request,
                                    RedirectAttributes redirectAttributes) {

        // Перед тем, как проверять данные пользователя и присылать код, мы должны убедиться
        // Его перенаправили или он сам постучался на этот endpoint, так как верификация
        // Должна идти строго после авторизации

        // Получаем username из сессии
        HttpSession session = request.getSession();
        String sessionName = (String) session.getAttribute("username");

        if(sessionName == null || !sessionName.equals(username)) {
            redirectAttributes.addFlashAttribute("message", "Вы не можете отправлять код на другие аккаунты!");
            return "redirect:/login";
        }

        // Проверяем, если имя пользователя пустое или отсутствует в запросе
        if(username == null || username.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Прежде чем подтвердить почту, авторизуйтесь");
            return "redirect:/login";
        }

        User user = userService.findByUsername(username).orElse(null);

        if(user == null) {
            redirectAttributes.addFlashAttribute("message", "Пользователь с таким именем не найден");
            return "redirect:/login";
        }

        // Перед проведением остальных операции мы сначала проверяем, подтвердил ли пользователь почту
        String userEmail = user.getEmail();

        boolean mailIsActivated = emailChecker.checkEmailInIdentity(userEmail);

        if(!mailIsActivated){
            model.addAttribute("message", "Для того, чтобы получить код, подтвердите почту");
            return "loginpage";
        }

        return "VerifyEmail";
    }

    @PostMapping("/email-verification/{username}/send-code")
    @Transactional
    public String sendCode(@PathVariable(name = "username") String username , Model model,
                           HttpServletResponse response,
                           RedirectAttributes redirectAttributes) {

        User user = userService.findByUsername(username).orElse(null);
        String verificationCode =  otpGenerator.generateOTP();

        if(user == null) {
            redirectAttributes.addFlashAttribute("message", "Пользователь не найден!");
            return "redirect:/login";
        }

        // Проверка, прошла ли минута перед отправкой новой
        if(user != null && user.getLastSendedCode() != null) {
            ZonedDateTime currentTime = ZonedDateTime.now();

            if(user.getLastSendedCode().plusMinutes(1).isAfter(currentTime)) {
                model.addAttribute("message", "Вы уже отправили код меньше минуты назад, подождите!");
                return "VerifyEmail";
            }
        }

        // Удаление старого кода верификации, если он существует
        if (user != null && user.getLastSendedCode() != null) {
            verificationCodeRepository.deleteByUser(user);
        }

        try{
            verificationCodeService.save(verificationCode, user.getUsername());
            System.out.println("Код: " + verificationCode + " сохранен в базу");
        }catch (NotFoundException e){
            redirectAttributes.addFlashAttribute("message", "Пользователь с таким именем не найден");
            return "redirect:/login";
        }

        String subject = "Verification Code";
        String message = "Hi " + user.getUsername() + "!\n" +
                "Your verification code is " + verificationCode + ".\n" +
                "\n" +
                "Enter this code in our site to activate your account.\n" +
                "\n" +
                "If you have any questions, send us an email shalgynbayramazan@gmail.com.\n" +
                "\n";

        // Сообщение активации
        mailSenderService.sendEmail(emailSender, user.getEmail(), subject, message);
        user.setLastSendedCode(ZonedDateTime.now());
        userService.updateUser(user);

        model.addAttribute("message", "Код отправлен!");
        return "VerifyEmail";
    }


    @PostMapping("/email-verification/{username}/check-code")
    public String checkCode(@PathVariable(name = "username") String username, HttpServletResponse response,
                            String code, Model model, RedirectAttributes redirectAttributes) {

        // Чтобы каждый раз не дергать код из базы мы сначала проверяем, нормальный ли код
        if(code.length() < 6) {
            model.addAttribute("message", "Код должен состоять из 6 цифр!");
            return "VerifyEmail";
        }

        // Так как код передается как строка, мы сначала должны проверить, точно ли пользователь ввел именно цифры
        if(!code.matches("[0-9]+")) {
            model.addAttribute("message", "Код должен состоять из цифр, без букв и пробелов!");
        }


        // Вытаскиваем код из базы по коду от пользователя
        VerificationCode verificationCode = verificationCodeRepository.findByCode(code).orElse(null);


        // Проверяем, найден ли код в базе данных
        if (verificationCode == null) {
            model.addAttribute("message", "Код неправильный!");
            return "VerifyEmail";
        }

        // Проверяем, использован ли уже этот код
        if (verificationCode.isUsed()) {
            model.addAttribute("message", "Данный код уже использован!");
            return "VerifyEmail";
        }

        boolean checkCode = false;

        try{
            checkCode = verificationCodeService.checkCode(code);
        }catch (NotFoundException e){
            model.addAttribute("message", "Код неправильный!");
            return "VerifyEmail";
        }

        if(checkCode) {
            model.addAttribute("message", "код правильный все норм");
            verificationCode.setUsed(true);
            verificationCodeRepository.save(verificationCode);

            UserDetails userDetails = userService.loadUserByUsername(verificationCode.getUser().getUsername());
            String token = jwtTokenUtils.generateToken(userDetails);

            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setPath("/blogs/");
            cookie.setMaxAge(86400); // 24 hours
            cookie.setHttpOnly(true);

            response.addCookie(cookie);

            return "redirect:/blogs/main";
        }else {
            model.addAttribute("message", "код неправильный");
            return "VerifyEmail";
        }
    }

}
