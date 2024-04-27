package com.website.blogs.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "uuid_codes")
public class UUID {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "expiration_date", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime expirationDate;

    @Column(name = "is_Activated")
    private boolean is_Activated;

    @Column(name = "user_id")
    private Long user_id;
}
