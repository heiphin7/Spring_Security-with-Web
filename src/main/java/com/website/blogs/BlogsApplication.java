package com.website.blogs;

import com.website.blogs.entity.User;
import com.website.blogs.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogsApplication {
	public static void main(String[] args) {
		SpringApplication.run(BlogsApplication.class, args);
	}
}
