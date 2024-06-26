package com.website.blogs.services;

import com.website.blogs.entity.Blog;
import com.website.blogs.entity.User;
import com.website.blogs.repository.BlogRepository;
import com.website.blogs.validations.URLchecker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService{
    private final BlogRepository blogRepository;
    private final URLchecker urLchecker;

    public Optional<Blog> findById(Long aLong) {
        return blogRepository.findById(aLong);
    }

    // JPA репозиторий делает CRUD - операции за нас, так что просто используем его готовые методы
    public String saveBlog(Blog blog){

        // Перед всеми проверками мы должны проверить все строки на null для оптимизации
        if(checkAllFields(blog)) {
            return "Заполните все поля";
        }

        // пустой author
        if(blog.getAuthor() == null) {
            return "Нету автора";
        }

        if(!urLchecker.isValidImageLink(blog.getImage())){
            return "Введите корректную ссылку!";
        }

        blogRepository.save(blog);
        return "Блог успешно сохранен!";
    }

    public Page<Blog> getAllBlogs(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return blogRepository.findAll(pageable);
    }

    public void deleteBlogById(Long id){
        blogRepository.deleteBlogById(id);
    }

    // private, так как будет использоваться только в данном методе
    private boolean checkAllFields(Blog blog) {
        if (blog == null) {
            return true;
        } else {
            return blog.getTitle() == null || blog.getAnons() == null || blog.getFulltext() == null
                    || blog.getImage() == null;
        }
    }
}