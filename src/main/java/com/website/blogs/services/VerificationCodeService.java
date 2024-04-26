package com.website.blogs.services;

import com.website.blogs.entity.User;
import com.website.blogs.entity.VerificationCode;
import com.website.blogs.repository.VerificationCodeRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserService userService;

    public void save(String code, String username) throws NotFoundException {
        VerificationCode verificationCode = new VerificationCode();

        // Время действия токена будет 3 минуты
        ZonedDateTime expirationDate = ZonedDateTime.now().plusMinutes(3);

        User user = userService.findByUsername(username).orElse(null);

        if(user == null) {
            throw new NotFoundException("Пользователь не найден!");
        }

        verificationCode.setExpirationDate(expirationDate);
        verificationCode.setCode(code);
        verificationCode.setUser(user);
        verificationCode.setUsed(false);
        verificationCode.setAttempts_count(0);

        verificationCodeRepository.save(verificationCode);
    }

    public boolean checkCode(String code) throws NotFoundException {
        VerificationCode codeForCheck = verificationCodeRepository.findByCode(code).orElse(null);

        if(codeForCheck == null) {
            throw new NotFoundException("Код не найден");
        }

        ZonedDateTime now = ZonedDateTime.now();

        String codeInDb = codeForCheck.getCode();
        ZonedDateTime expirationDateOfCode = codeForCheck.getExpirationDate();

        // Если код пользователя и код в базе не совпадает
        if(!codeInDb.equals(code)) {
            return false;
        }

        // Если код уже использован
        if(codeForCheck.isUsed()) {
            return false;
        }

        // Если код истек
        if(now.isAfter(expirationDateOfCode)) {
            return false;
        }

        return true;
    }
}
