package com.website.blogs.repository;

import com.website.blogs.entity.User;
import com.website.blogs.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    void deleteByUser(User user);
    Optional<VerificationCode> findByCode(String code);
}
