package com.stichj.urlshortener.repository;

import com.stichj.urlshortener.model.URLMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<URLMapping, Long> {
    Optional<URLMapping> findByShortCode(String shortCode);

}
