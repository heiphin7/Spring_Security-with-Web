package com.website.blogs.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class AddBlogDTO {

    @Size(min = 10, max = 50, message = "Название должно быть от 10 до 50 символов")
    private String title;

    @Size(min = 50, max = 200, message = "Анонс должен быть от 50 до 200 символов")
    private String anons;

    @Size(min = 1500, message = "Статья слишком короткая, напишите подробней")
    private String fulltext;

    @URL(message = "Вставьте правильную ссылку")
    private String image;

}
