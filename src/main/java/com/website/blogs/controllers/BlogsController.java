package com.website.blogs.controllers;

import com.website.blogs.dtos.AddBlogDTO;
import com.website.blogs.entity.Blog;
import com.website.blogs.entity.User;
import com.website.blogs.services.BlogService;
import com.website.blogs.services.UserService;
import com.website.blogs.validations.URLchecker;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blogs/")
public class BlogsController {
    private final URLchecker urLchecker;
    private final BlogService blogService;
    private final UserService userService;
    @GetMapping("/add")
    public String addBlogPage(Model model, AddBlogDTO addBlogDTO){
        model.addAttribute("blog", addBlogDTO);
        Optional<User> optionalUser = userService.getCurrentUser();
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

        Optional<User> optionalUser = userService.getCurrentUser();
        User user = optionalUser.orElseThrow(() -> new UsernameNotFoundException("Current User not found"));

        Blog blog = new Blog(addBlogDTO.getTitle(), addBlogDTO.getAnons(), addBlogDTO.getFulltext(), addBlogDTO.getImage(), user);
        blogService.saveBlog(blog);
        return "redirect:/blogs/main";
    }

    @GetMapping("/{id}")
    public String blogDetails(@PathVariable(name = "id") long id ,Model model) throws NotFoundException{
        Optional<Blog> optionalBlog = blogService.findById(id);
        Blog blog = optionalBlog.orElseThrow(() -> new NotFoundException("Blog not found"));
        model.addAttribute("blog", blog);

        return "blog-details";
    }
}
