package com.website.blogs.controllers;

import com.website.blogs.entity.Blog;
import com.website.blogs.entity.User;
import com.website.blogs.services.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final BlogService blogService;
    @GetMapping("/blogs/main")
    public String getMainpage(@RequestParam(defaultValue = "0") int page, Model model){
        Page<Blog> blogPage =  blogService.getAllBlogs(page, 10);
        model.addAttribute("blogs", blogPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        return "main";
    }

    @GetMapping("/")
    public String mainPage() {
        return "redirect:/login";
    }
}
