package com.website.blogs.api;

import org.springframework.stereotype.Component;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.model.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;

import java.io.IOException;

@Component
public class EmailChecker {
    private static final String API_KEY = "dcgfzvrR3fBvdAZrpeZSS";
    public boolean checkEmail(String email) {

        // Заранее инициализиурем переменную, где будем хранить ответ
        String response = "";

        // URL для проверки адреса электронной почты, взятый из API
        String url =
                "https://apps.emaillistverify.com/api/verifEmail?secret=" + API_KEY + "&email=" + email;

        // Создаем HTTP-клиент
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // Создаем GET-запрос
            HttpGet request = new HttpGet(url);

            // Отправляем запрос и получаем ответ
            response = httpClient.execute(request, httpResponse -> {
                // Печатаем тело ответа
                return EntityUtils.toString(httpResponse.getEntity());

            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        /* При проверке почты, API может выдавать очень много ошибок:
         *  Например: Invalid Syntax, Domain not found, Mailbox full
         * А правильный ответ только 1 - ok, поэтому просто сверяем ответ с ok
         */

        return response.equals("ok");
    }
}
