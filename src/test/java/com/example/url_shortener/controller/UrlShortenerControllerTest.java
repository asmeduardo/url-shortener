package com.example.url_shortener.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.url_shortener.model.ShortenedUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LinkShortenerControllerTest {

    @Mock
    private UrlShortenerService service;

    @InjectMocks
    private UrlShortenerController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShortenLinkSuccess() {
        ShortenedUrl link = new ShortenedUrl();
        link.setOriginalLink("http://example.com");
        link.setShortenedUrl("abcd1234");

        when(service.createShortenedUrl("http://example.com", 10)).thenReturn(link);

        ShortenedUrl result = controller.shortenUrl("http://example.com", 10);

        assertNotNull(result);
        assertEquals("http://example.com", result.getOriginalLink());
    }

    @Test
    void testShortenLinkInvalidUrl() {
        when(service.createShortenedUrl("invalid-url", 10)).thenThrow(new IllegalArgumentException("URL inválida."));

        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            controller.shortenUrl("invalid-url", 10);
        });

        assertEquals(HttpStatus.BAD_REQUEST, ((ResponseStatusException) exception).getStatus());
    }

    @Test
    void testGetOriginalUrlSuccess() {
        ShortenedUrl link = new ShortenedUrl();
        link.setOriginalLink("http://example.com");

        when(service.getOriginalUrl("abcd1234")).thenReturn(link);

        String result = controller.getOriginalUrl("abcd1234");

        assertEquals("http://example.com", result);
    }

    @Test
    void testGetOriginalUrlNotFound() {
        when(service.getOriginalUrl("abcd1234")).thenReturn(null);

        String result = controller.getOriginalUrl("abcd1234");

        assertEquals("Link expirado ou não encontrado", result);
    }
}
