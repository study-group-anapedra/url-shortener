package org.anasantana.service;

import org.anasantana.domain.UrlShortener;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.repository.UrlShortenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlShortenerServiceTest {
    private UrlShortenerRepository repository;
    private UrlShortenerService service;

    @BeforeEach
    void setup() {
        repository = mock(UrlShortenerRepository.class);
        service = new UrlShortenerService(repository);
    }

    @Test
    void deveRetornarMesmoCodigoParaUrlJaExistente() {
        String urlOriginal = "https://www.google.com";
        UrlShortener urlExistente = new UrlShortener(urlOriginal, "abc123");
        
        when(repository.findByOriginalUrl(urlOriginal)).thenReturn(Optional.of(urlExistente));

        UrlShortenerDTO resultado = service.encurtar(urlOriginal, "usuario-teste");

        assertEquals("abc123", resultado.getShortCode()); // Compara apenas a String [cite: 2025-12-23]
        verify(repository, never()).save(any());
    }
}