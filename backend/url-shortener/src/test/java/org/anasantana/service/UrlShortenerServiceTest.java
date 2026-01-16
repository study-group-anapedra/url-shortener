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
        // 1. Preparação
        String urlOriginal = "https://www.google.com";
        UrlShortener urlExistente = new UrlShortener(urlOriginal, "abc123");

        // Criamos o DTO que o Service agora exige
        UrlShortenerDTO dtoEntrada = new UrlShortenerDTO();
        dtoEntrada.setOriginalUrl(urlOriginal);

        when(repository.findByOriginalUrl(urlOriginal)).thenReturn(Optional.of(urlExistente));

        // 2. Execução (Passando o objeto DTO em vez da String)
        UrlShortenerDTO resultado = service.encurtar(dtoEntrada, "usuario-teste");

        // 3. Verificações
        assertEquals("abc123", resultado.getShortCode());
        verify(repository, never()).save(any());
    }
}