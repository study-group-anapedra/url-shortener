package org.anasantana.resource;

import jakarta.ws.rs.core.Response;
import org.anasantana.domain.UrlShortener;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.repository.UrlShortenerRepository;
import org.anasantana.service.UrlShortenerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UrlShortenerResourceTest {

    @Mock
    private UrlShortenerService service;

    @InjectMocks
    private UrlShortenerResource resource;

    @BeforeEach
    void setUp() {
        // Garante que o service mockado seja injetado no resource
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRetornarStatus201AoEncurtarComSucesso() {
        // 1. Dados de entrada (conforme as imagens do seu código)
        String urlOriginal = "https://google.com";
        String clientId = "cliente-teste";
        UrlShortenerDTO inputDto = new UrlShortenerDTO(urlOriginal, null);

        // 2. Objeto que o Service DEVE retornar (Não pode ser null)
        UrlShortenerDTO mockResult = new UrlShortenerDTO(urlOriginal, "ABC1234");
        
        // Configura o mock para retornar o objeto preenchido
        when(service.encurtar(anyString(), anyString())).thenReturn(mockResult);

        // 3. Execução
        Response response = resource.encurtar(inputDto, clientId);

        // 4. Verificações (Onde dava o erro de null)
        Assertions.assertEquals(201, response.getStatus()); 
        Assertions.assertNotNull(response.getEntity(), "O corpo da resposta não deveria ser nulo");
        
        UrlShortenerDTO entidadeRetornada = (UrlShortenerDTO) response.getEntity();
        Assertions.assertEquals("ABC1234", entidadeRetornada.getShortCode());
    }
}
