package com.website.blogs.repository;

import com.website.blogs.entity.Blog;
import com.website.blogs.entity.UserFavoriteBlogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteBlogsRepository extends JpaRepository<UserFavoriteBlogs, Long> {

    @Query("SELECT b FROM Blog b WHERE b.id IN (SELECT ufb.blogId FROM UserFavoriteBlogs ufb WHERE ufb.userId = ?1)")
    List<Blog> findBlogsByUserId(Long userId);

    boolean existsByUserIdAndBlogId(Long userId, Long blogId);

    Optional<UserFavoriteBlogs> findByUserIdAndBlogId(Long userId, Long blogId);
}

