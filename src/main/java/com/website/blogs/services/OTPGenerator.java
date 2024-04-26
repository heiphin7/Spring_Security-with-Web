package com.website.blogs.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPGenerator {

    public String generateOTP() {

        // Генерируем случайный 6-значный код
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
