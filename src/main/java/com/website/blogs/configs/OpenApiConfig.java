package com.website.blogs.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

// Настройки для отображения Swagger-ui
@OpenAPIDefinition(
        info = @Info(
                title = "Spring-Web Backend API",
                description = "Это Backend API для взаимодействия с Fronted",
                contact = @Contact(
                        name = "@heiphin7",
                        email = "r.shalgin@gmail.com"
                ),
                version = "v2"
        )
)
public class OpenApiConfig {
}
