package com.website.blogs.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "verification_code")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "expiration_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime expirationDate;

    @Column(name = "isUsed")
    private boolean isUsed;

    @OneToOne
    @JoinTable(
            name = "users_codes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "verificationCode_id")
    )
    private User user;

    @Column(name = "attempts")
    private int attempts_count;
}
