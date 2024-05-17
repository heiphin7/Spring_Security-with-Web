package com.website.blogs;

import com.website.blogs.repository.VerificationCodeRepository;
import com.website.blogs.services.OTPGenerator;
import com.website.blogs.services.UserService;
import com.website.blogs.services.VerificationCodeService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class VerificationServiceTest {
    @InjectMocks
    private static OTPGenerator otpGenerator;
    @InjectMocks
    private static VerificationCodeService verificationCodeService;
    @Mock
    private static UserService userService;
    @Mock
    private static VerificationCodeRepository verificationCodeRepository;
    @BeforeAll
    public static void init() {
        verificationCodeRepository = Mockito.mock(VerificationCodeRepository.class);
        userService = Mockito.mock(UserService.class);

        verificationCodeService = new VerificationCodeService(
                verificationCodeRepository, userService
        );
        otpGenerator = new OTPGenerator();
    }

    @Test
    public void generateCode() {
        // arrange
        // Базовая длина кода из otpGenerator
        int lenght = 6;

        // act
        String randomCode = otpGenerator.generateOTP();

        // assert
        Assert.assertEquals(6, randomCode.length());
    }

    @Test
    public void send_empty_code() {
        // arrange
        String code = null;

        // Возвращаем null, так как код пустой и его не может быть в базе данных
        Mockito.when(verificationCodeRepository.findByCode(code)).thenReturn(null);

        // act
        boolean codeIsCorrect = verificationCodeService.checkCode(code);

        /* Так как code равен null, тогда у нас должно быть false
        *  act *                                                                                                                                                                                                                                                  */
        Assert.assertFalse(codeIsCorrect);
    }

}
