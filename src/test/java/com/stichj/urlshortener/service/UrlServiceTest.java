package com.stichj.urlshortener.service;

import com.stichj.urlshortener.model.UrlMapping;
import com.stichj.urlshortener.repository.UrlRepository;
import com.stichj.urlshortener.util.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlService urlService;

    @Test
    void shortenUrl_shouldGenerateShortCodeAndSaveMapping() {
        String originalUrl = "https://example.com";

        when(urlRepository.save(any(UrlMapping.class))).thenAnswer(invocation -> {
            UrlMapping mapping = invocation.getArgument(0);
            mapping.setId(1L); // Simulate DB-generated ID
            return mapping;
        });


        ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);

        UrlMapping result = urlService.shortenUrl(originalUrl);

        verify(urlRepository, times(2)).save(captor.capture());

        UrlMapping savedMapping = captor.getAllValues().get(1);
        assertEquals(originalUrl, savedMapping.getOriginalUrl());
        assertNotNull(savedMapping.getShortCode());
        assertEquals(result.getShortCode(), savedMapping.getShortCode());
        assertNotNull(result);

    }

    @Test
    void getOriginalUrl_shouldReturnTheOriginalUrlFromAGivenShortCode() {
        String url = "https://example.com";
        String shortCode = "123abc";

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(url);
        urlMapping.setId(1L);
        urlMapping.setShortCode(shortCode);

        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.of(urlMapping));

        String originalUrl = urlService.getOriginalUrl(url);

        assertEquals(url, originalUrl);

    }

    @Test
    void getOriginalUrl_shouldThrowExceptionIfShortCodeNotFound() {
        String falseShortCode = "notContained";

        when(urlRepository.findByShortCode(falseShortCode)).thenReturn(Optional.empty());

        UrlNotFoundException exception = assertThrows(
                UrlNotFoundException.class,
                () -> urlService.getOriginalUrl(falseShortCode)
        );

        assertEquals("Short code not found: notContained", exception.getMessage());
    }
}
