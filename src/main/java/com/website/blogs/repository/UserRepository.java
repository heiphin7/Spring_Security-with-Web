package com.website.blogs.repository;

import com.website.blogs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String username);

    Optional<User> findByEmailAndUsername(String username, String email);
}
