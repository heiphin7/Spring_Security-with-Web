package com.website.blogs.repository;

import com.website.blogs.entity.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UUIDRepository extends JpaRepository<UUID, Long> {

}
