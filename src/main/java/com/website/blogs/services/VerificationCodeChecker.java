package com.website.blogs.services;

import com.website.blogs.controllers.RegistrationController;
import com.website.blogs.entity.VerificationCode;
import com.website.blogs.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class VerificationCodeChecker {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final VerificationCodeRepository verificationCodeRepository;

    // Аннотация scheduled - означает что данный поток "запланированный"
    // то есть данный поток будет выполняться автоматически каждые 3 минуты
   @Scheduled(fixedRate = 180000)
    public void VerificationCodeCheck() {

        logger.info("Началась проверка токенов............");

        List<VerificationCode> verificationCodeList = verificationCodeRepository.findAll();

        for(VerificationCode verificationCode: verificationCodeList) {

            // Если код истек и не использован, тогда мы можем его смело удалять
            if (ZonedDateTime.now().isAfter(verificationCode.getExpirationDate()) && !verificationCode.isUsed()) {
                logger.info("Токен с id: " + verificationCode.getId() + " удалён, так как не использован и истек");

                verificationCodeRepository.delete(verificationCode);
            }

            // Если код уже использован тогда мы можем архивировать его
            if(verificationCode.isUsed()) {
                // TODO что делать если использован

            }

        }

        logger.info("Проверка токенов закончилась..............");
    }
}
