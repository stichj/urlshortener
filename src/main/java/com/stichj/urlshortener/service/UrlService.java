package com.stichj.urlshortener.service;

import com.stichj.urlshortener.model.UrlMapping;
import com.stichj.urlshortener.repository.UrlRepository;
import com.stichj.urlshortener.util.exceptions.UrlNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UrlService {
    UrlRepository urlRepository;

    private static final String BASE62 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlMapping shortenUrl(String originalUrl) {
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);

        UrlMapping savedMapping = urlRepository.save(urlMapping);

        String shortCode = generateBase62ShortCode(savedMapping.getId());

        savedMapping.setShortCode(shortCode);

        return savedMapping;

    }

    private String generateBase62ShortCode(Long id) {
        StringBuilder builder = new StringBuilder();

        while (id > 0) {
            builder.append(BASE62.charAt((int) (id % 62)));
            id /= 62;
        }

        return builder.reverse().toString();

    }

    @Cacheable(value = "shortLinks", key = "#shortCode")
    public String getOriginalUrl(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new UrlNotFoundException("Short code not found: " + shortCode));
    }

    private String generateShortCode() {
        int shortCodeLength = 6;
        StringBuilder shortCode = new StringBuilder();
        Random random = new Random();

        String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        for (int i=0; i<shortCodeLength; i++) {
            int randomInt = random.nextInt(allowedChars.length());
            shortCode.append(allowedChars.charAt(randomInt));
        }

        return shortCode.toString();
    }
}
