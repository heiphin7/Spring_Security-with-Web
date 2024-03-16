package com.website.blogs.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class AddBlogDTO {

    @Size(min = 10, message = "Опишите название более подробно")
    @Max(value = 50, message = "Назовите статью более короче")
    private String title;

    @Size(max = 200, message = "Сделайте анонс более кратким")
    @Min(value = 100, message = "Опишите анонс более подробнее")
    private String anons;

    @Size(min = 2000, message = "Статья слишком короткая, напишите подробней")
    private String fulltext;

    @URL(message = "Вставьте правильную ссылку")
    private String image;

}
