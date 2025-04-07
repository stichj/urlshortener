package com.stichj.urlshortener.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class URLMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String shortCode;

    @Column(nullable = false)
    private String url;

    private LocalDateTime createdAt;

    public URLMapping() {
        this.createdAt = LocalDateTime.now();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getUrl() {
        return url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
