package com.website.blogs.controllers;

import com.website.blogs.dtos.AddBlogDTO;
import com.website.blogs.entity.Blog;
import com.website.blogs.services.BlogService;
import com.website.blogs.validations.URLchecker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class BlogsController {
    private final URLchecker urLchecker;
    private final BlogService blogService;
    @GetMapping("/add")
    public String addBlogPage(Model model, AddBlogDTO addBlogDTO){
        model.addAttribute("blog", addBlogDTO);
        return "Add-Post";
    }

    @PostMapping("/add")
    public String addNewPost(@Valid AddBlogDTO addBlogDTO, BindingResult errors) {
        if(errors.hasErrors()){
            return "Add-Post";
        }

        if(!urLchecker.isValidImageLink(addBlogDTO.getImage())){
            errors.rejectValue("image", "incorrectImage", "Введите корректный URL");
            return "Add-Post";
        }
        Blog blog = new Blog(addBlogDTO.getTitle(), addBlogDTO.getAnons(), addBlogDTO.getFulltext(), addBlogDTO.getImage());
        blogService.saveBlog(blog);
        return "redirect:/main";
    }
}
