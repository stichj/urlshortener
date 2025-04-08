package com.stichj.urlshortener.controller;

import com.stichj.urlshortener.model.UrlMapping;
import com.stichj.urlshortener.service.UrlService;
import com.stichj.urlshortener.util.exceptions.UrlNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
public class UrlControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    void shortenUrl_shouldReturnShortCode() throws Exception {
        String originalUrl = "https://example.com";
        String shortCode = "123abc";

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortCode(shortCode);

        when(urlService.shortenUrl(originalUrl)).thenReturn(urlMapping);

        mockMvc.perform(post("/shorten")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"originalUrl\": \"https://example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode").value("123abc"));

    }

    @Test
    void getOriginalUrl_shouldReturnOriginalUrl() throws Exception {
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl("https://example.com");
        mapping.setShortCode("123abc");

        when(urlService.getOriginalUrl("123abc")).thenReturn("https://example.com");

        mockMvc.perform(get("/123abc"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void getOriginalUrl_shouldReturnNotFoundIfShortCodeMissing() throws Exception {
        when(urlService.getOriginalUrl("missing")).thenThrow(new UrlNotFoundException("Short code not found: missing"));

        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound());
    }
}
