package com.website.blogs.controllers;

import com.website.blogs.dtos.AddBlogDTO;
import com.website.blogs.entity.Blog;
import com.website.blogs.entity.User;
import com.website.blogs.services.BlogService;
import com.website.blogs.services.UserFavoriteBlogsService;
import com.website.blogs.services.UserService;
import com.website.blogs.validations.URLchecker;
import jakarta.validation.Valid;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blogs/")
public class BlogsController {
    private final URLchecker urLchecker;
    private final BlogService blogService;
    private final UserService userService;
    private final UserFavoriteBlogsService userFavoriteBlogsService;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping("/add")
    public String addBlogPage(Model model, AddBlogDTO addBlogDTO){
        model.addAttribute("blog", addBlogDTO);
        userService.getCurrentUser();
        return "Add-Post";
    }

    @PostMapping("/add")
    public String saveNewBlog(@Valid AddBlogDTO addBlogDTO, BindingResult errors) {
        if(errors.hasErrors()){
            return "Add-Post";
        }

        if(!urLchecker.isValidImageLink(addBlogDTO.getImage())){
            errors.rejectValue("image", "incorrectImage", "Введите корректный URL");
            return "Add-Post";
        }

        User user = userService.getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("Current User not found"));

        Blog blog = new Blog(addBlogDTO.getTitle(), addBlogDTO.getAnons(), addBlogDTO.getFulltext(), addBlogDTO.getImage(), user);
        blogService.saveBlog(blog);

        logger.info("Добавлен новый блог! id: " + blog.getId());
        return "redirect:/blogs/main";
    }

    @GetMapping("/{id}")
    public String blogDetails(@PathVariable(name = "id") long id ,Model model) throws NotFoundException{
        Blog blog = blogService.findById(id)
                .orElseThrow(() -> new NotFoundException("Blog not found"));
        model.addAttribute("blog", blog);
        return "blog-details";
    }

    @GetMapping("/saved-blogs")
    public String savedBlogs(Model model){

        User user = userService.getCurrentUser().orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Blog> savedBlogs = userFavoriteBlogsService.getFavoriteBlogsByUserId(user.getId());

        model.addAttribute("savedBlogs", savedBlogs);
        return "saved-blogs";
    }

    @GetMapping("/add-to-favorites/{blogId}")
    public String addToFavorite(@PathVariable(name = "blogId") Long blogId, RedirectAttributes redirectAttributes) throws NotFoundException {
        User user = userService.getCurrentUser()
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Long> favoriteBlogs = new ArrayList<>(user.getFavoriteBlogs());

        if (!favoriteBlogs.contains(blogId)) {
            favoriteBlogs.add(blogId);
            user.setFavoriteBlogs(new ArrayList<>(favoriteBlogs));  // Создаем новый список и устанавливаем его в сущность User

            userService.updateFavoriteBlogs(user, user);
            redirectAttributes.addFlashAttribute("success", "Блог успешно добавлен в избранное!");
        } else {
            redirectAttributes.addFlashAttribute("erorr", "Вы уже добавляли данный блог в избранное!");
        }

        return "redirect:/blogs/" + blogId;
    }

    /*
    *  Метод для удаления блога из избранных
    */

    @GetMapping("/remove/{blogId}")
    public String removeFromFavorites(@PathVariable(name = "blogId") Long blogId, Model model, RedirectAttributes redirectAttributes) {
        // Сначала мы должны проверить, добавлял ли пользователь данные блог ранее, чтобы избежать ошибки
        // Для этого, мы сначала должны получить текущего пользователя

        User user = userService.getCurrentUser().orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        // Используя метод сервиса, проверяем есть ли блог у пользователя
        boolean blogAdded = userFavoriteBlogsService.hasUserSavedBlog(user.getId(), blogId);

        // Если имеется, то удаляем. В противаном случае - выдаем сообщение об ошибке
        if(blogAdded){
            userFavoriteBlogsService.removeBlogFromFavorites(user.getId(), blogId);
            redirectAttributes.addFlashAttribute("success", "Блог успешно удален из избранных");
        }else{
            redirectAttributes.addFlashAttribute("erorr", "Вы не сохраняли данный блог!");
        }

        return "redirect:/blogs/" + blogId;
    }


}
