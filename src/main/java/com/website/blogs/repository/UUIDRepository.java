package com.website.blogs.repository;

import com.website.blogs.entity.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UUIDRepository extends JpaRepository<UUID, Long> {
    Optional<UUID> findByUuid(String uuid);

    List<UUID> findAllByUserId(Long userId);
}
