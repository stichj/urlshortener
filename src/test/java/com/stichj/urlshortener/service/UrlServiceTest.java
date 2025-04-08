package com.stichj.urlshortener.service;

import com.stichj.urlshortener.model.UrlMapping;
import com.stichj.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlService urlService;

    @Test
    void shortenUrl_shouldGenerateShortCodeAndSaveMapping() {
        String originalUrl = "https://example.com";

        when(urlRepository.findByShortCode(anyString())).thenReturn(Optional.empty());

        ArgumentCaptor<UrlMapping> captor = ArgumentCaptor.forClass(UrlMapping.class);

        UrlMapping result = urlService.shortenUrl(originalUrl);

        verify(urlRepository).save(captor.capture());

        UrlMapping savedMapping = captor.getValue();
        assertEquals(originalUrl, savedMapping.getOriginalUrl());
        assertNotNull(savedMapping.getShortCode());
        assertEquals(result.getShortCode(), savedMapping.getShortCode());
        assertFalse(result == null);

    }
}
