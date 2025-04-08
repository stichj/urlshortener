package com.stichj.urlshortener.controller;

import com.stichj.urlshortener.dto.UrlRequest;
import com.stichj.urlshortener.model.UrlMapping;
import com.stichj.urlshortener.service.UrlService;
import com.stichj.urlshortener.util.exceptions.UrlNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UrlController {
    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> shortenUrl(@RequestBody UrlRequest urlRequest) {
        String originalUrl = urlRequest.getOriginalUrl();
        UrlMapping mapping = urlService.shortenUrl(originalUrl);

        return new ResponseEntity<>(mapping, HttpStatus.CREATED);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getOriginalUrl(@PathVariable String shortCode) {
        try {
            String originalUrl = urlService.getOriginalUrl(shortCode);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", originalUrl);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (UrlNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
