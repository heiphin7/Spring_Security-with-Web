package com.website.blogs.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "blog")
public class Blog {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "anons", columnDefinition = "TEXT")
    private String anons;

    @Column(name = "fulltext", columnDefinition = "TEXT")
    private String fulltext;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    public Blog(){}

    public Blog(String title, String anons, String fulltext, String image, User author){
        this.title = title;
        this.anons = anons;
        this.fulltext = fulltext;
        this.image = image;
        this.author = author;
    }
    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", anons='" + anons + '\'' +
                ", fulltext='" + fulltext + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
