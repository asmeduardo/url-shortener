package com.example.url_shortener.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.util.Optional;

public class UrlShortenerServiceTest {

    @Mock
    private ShortenedUrlRepository repository;

    @InjectMocks
    private UrlShortenerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateShortenedUrlSuccess() {
        String originalUrl = "http://example.com";
        int durationInMinutes = 10;

        when(repository.findByShortenedUrl(anyString())).thenReturn(Optional.empty());

        ShortenedUrl link = service.createShortenedLink(originalLink, durationInMinutes);

        assertNotNull(link);
        assertEquals(originalLink, link.getOriginalLink());
        assertNotNull(link.getShortenedLink());
        assertTrue(link.getExpirationDate().isAfter(link.getCreationDate()));
        verify(repository, times(1)).save(link);
    }

    @Test
    void testCreateShortenedLinkInvalidUrl() {
        String invalidLink = "invalid-url";
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.createShortenedLink(invalidLink, 10);
        });

        assertEquals("URL inválida.", exception.getMessage());
    }

    @Test
    void testGetOriginalLink() {
        ShortenedLink link = new ShortenedLink();
        link.setOriginalLink("http://example.com");
        link.setShortenedLink("abcd1234");
        link.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        when(repository.findByShortenedLink("abcd1234")).thenReturn(Optional.of(link));

        ShortenedLink result = service.getOriginalLink("abcd1234");

        assertNotNull(result);
        assertEquals("http://example.com", result.getOriginalLink());
    }

    @Test
    void testGetOriginalLinkExpired() {
        ShortenedLink link = new ShortenedLink();
        link.setShortenedLink("abcd1234");
        link.setExpirationDate(LocalDateTime.now().minusMinutes(10));

        when(repository.findByShortenedLink("abcd1234")).thenReturn(Optional.of(link));

        ShortenedLink result = service.getOriginalLink("abcd1234");

        assertNull(result);
    }

    @Test
    void testDeleteExpiredLinks() {
        doNothing().when(repository).deleteByExpirationDateBefore(any(LocalDateTime.class));

        service.deleteExpiredLinks();

        verify(repository, times(1)).deleteByExpirationDateBefore(any(LocalDateTime.class));
    }
}

