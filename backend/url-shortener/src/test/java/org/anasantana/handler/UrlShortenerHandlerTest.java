package org.anasantana.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.service.UrlShortenerService;
import org.anasantana.service.exception.BusinessException;
import org.anasantana.service.exception.InfrastructureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UrlShortenerHandlerTest {

    private UrlShortenerService service;
    private UrlShortenerHandler handler;
    private Context context;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    void setup() {
        service = Mockito.mock(UrlShortenerService.class);
        handler = new UrlShortenerHandler(service);

        context = Mockito.mock(Context.class);

        // Mock correto do logger da Lambda
        LambdaLogger logger = Mockito.mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void deveRetornar400QuandoBusinessException() throws Exception {
        UrlShortenerDTO dto = new UrlShortenerDTO();
        dto.setOriginalUrl("file:///etc/passwd");

        when(service.encurtar(anyString(), any()))
                .thenThrow(new BusinessException("Apenas URLs com http ou https são permitidas"));

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withPath("/url")
                .withHeaders(Map.of("Host", "api.asantanadev.com"))
                .withBody(mapper.writeValueAsString(dto));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Apenas URLs com http ou https são permitidas"));
    }
    @Test
    void deveRetornar500QuandoInfrastructureException() throws Exception {
        UrlShortenerDTO dto = new UrlShortenerDTO();
        dto.setOriginalUrl("https://google.com");

        when(service.encurtar(anyString(), any()))
                .thenThrow(new InfrastructureException(
                        "Erro no DynamoDB",
                        new RuntimeException("Falha interna")
                ));

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withPath("/url")
                .withHeaders(Map.of("Host", "api.asantanadev.com"))
                .withBody(mapper.writeValueAsString(dto));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Erro no DynamoDB"));
    }


    @Test
    void deveCriarUrlComSucesso() throws Exception {
        UrlShortenerDTO retorno = new UrlShortenerDTO();
        retorno.setShortCode("abc123");

        when(service.encurtar(anyString(), any())).thenReturn(retorno);

        UrlShortenerDTO dto = new UrlShortenerDTO();
        dto.setOriginalUrl("https://google.com");

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withHttpMethod("POST")
                .withPath("/url")
                .withHeaders(Map.of("Host", "api.asantanadev.com"))
                .withBody(mapper.writeValueAsString(dto));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().contains("https://api.asantanadev.com/abc123"));
    }
}
