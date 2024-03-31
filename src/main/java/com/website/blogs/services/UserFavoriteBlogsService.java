package com.website.blogs.services;

import com.website.blogs.entity.Blog;
import com.website.blogs.entity.UserFavoriteBlogs;
import com.website.blogs.repository.UserFavoriteBlogsRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserFavoriteBlogsService {
    private final UserFavoriteBlogsRepository userFavoriteBlogsRepository;

    public List<Blog> getFavoriteBlogsByUserId(Long userId) {
        return userFavoriteBlogsRepository.findBlogsByUserId(userId);
    }

    public boolean hasUserSavedBlog(Long userId, Long blogId) {
        return userFavoriteBlogsRepository.existsByUserIdAndBlogId(userId, blogId);
    }

    public void removeBlogFromFavorites(Long userId, Long blogId) {
        Optional<UserFavoriteBlogs> userFavoriteBlog = userFavoriteBlogsRepository.findByUserIdAndBlogId(userId, blogId);
        userFavoriteBlog.ifPresent(blog -> userFavoriteBlogsRepository.delete(blog));
    }
}

