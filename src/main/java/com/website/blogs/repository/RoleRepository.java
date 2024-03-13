package com.website.blogs.repository;

import com.website.blogs.entity.Role;
import com.website.blogs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
