package com.website.blogs.services;

import com.website.blogs.entity.Blog;
import com.website.blogs.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService{
    private final BlogRepository blogRepository;

    public Optional<Blog> findById(Long aLong) {
        return blogRepository.findById(aLong);
    }

    public void saveBlog(Blog blog){
        blogRepository.save(blog);
    }

    public Page<Blog> getAllBlogs(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return blogRepository.findAll(pageable);
    }

    public void deleteBlogById(Long id){
        blogRepository.deleteBlogById(id);
    }
}
