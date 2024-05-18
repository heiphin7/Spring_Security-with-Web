package com.website.blogs.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

// Настройки для отображения Swagger-ui
@OpenAPIDefinition(
        info = @Info(
                title = "Название API",
                description = "Описание для API",
                termsOfService = "Правила использования",
                contact = @Contact(
                        name = "Название контакта",
                        email = "email контакта"
                ),
                license = @License(
                        name = "Название лицензии"
                ),
                version = "v1"
        )
)
public class OpenApiConfig {
}
