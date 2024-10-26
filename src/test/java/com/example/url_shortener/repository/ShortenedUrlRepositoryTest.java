package com.example.url_shortener.repository;

import com.example.url_shortener.model.ShortenedUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ShortenedUrlRepositoryTest {

    @Autowired
    private ShortenedUrlRepository repository;

    @Test
    void testSaveAndFindByShortenedUrl() {
        ShortenedUrl url = new ShortenedUrl();
        url.setOriginalLink("http://example.com");
        url.setShortenedUrl("abcd1234");
        url.setCreationDate(LocalDateTime.now());
        url.setExpirationDate(LocalDateTime.now().plusMinutes(10));

        repository.save(url);

        Optional<ShortenedUrl> found = repository.findByShortenedUrl("abcd1234");

        assertTrue(found.isPresent());
        assertEquals("http://example.com", found.get().getOriginalUrl());
    }

    @Test
    void testDeleteByExpirationDateBefore() {
        ShortenedUrl url = new ShortenedUrl();
        url.setOriginalLink("http://example.com");
        url.setShortenedUrl("abcd1234");
        url.setCreationDate(LocalDateTime.now().minusMinutes(20));
        url.setExpirationDate(LocalDateTime.now().minusMinutes(10));

        repository.save(url);
        repository.deleteByExpirationDateBefore(LocalDateTime.now());

        Optional<ShortenedUrl> found = repository.findByShortenedUrl("abcd1234");

        assertFalse(found.isPresent());
    }
}
