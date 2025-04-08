package com.stichj.urlshortener.service;

import com.stichj.urlshortener.model.UrlMapping;
import com.stichj.urlshortener.repository.UrlRepository;
import com.stichj.urlshortener.util.exceptions.UrlNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UrlService {
    UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlMapping shortenUrl(String originalUrl) {
        String shortCode = generateShortCode();
        while (urlRepository.findByShortCode(shortCode).isPresent()) {
            shortCode = generateShortCode();
        }
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortCode(shortCode);

        urlRepository.save(urlMapping);

        return urlMapping;

    }

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
