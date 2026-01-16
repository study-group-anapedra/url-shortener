package org.anasantana.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.anasantana.annotation.utils.orm.EntityManagerSimples;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.repository.UrlShortenerRepository;
import org.anasantana.service.UrlShortenerService;
import org.anasantana.service.exception.BusinessException;
import org.anasantana.service.exception.InfrastructureException;

import java.util.Map;

public class UrlShortenerHandler implements RequestHandler<
        APIGatewayProxyRequestEvent,
        APIGatewayProxyResponseEvent> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final UrlShortenerService service;

    // Construtor real (produção)
    public UrlShortenerHandler() {
        EntityManagerSimples entityManager = new EntityManagerSimples();
        UrlShortenerRepository repository = new UrlShortenerRepository(entityManager);
        this.service = new UrlShortenerService(repository);
    }

    // Construtor para testes
    public UrlShortenerHandler(UrlShortenerService service) {
        this.service = service;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        try {
            String method = request.getHttpMethod();
            String path = request.getPath();

            if ("POST".equalsIgnoreCase(method) && path.endsWith("/url")) {
                return handlePost(request);
            }

            if ("GET".equalsIgnoreCase(method) && path != null && !path.equals("/")) {
                return handleGet(request);
            }

            return response(404, "{\"error\":\"Endpoint não encontrado\"}");
        }

        // Qualquer erro de regra de negócio (URL inválida, regex não bate, etc)
        catch (BusinessException e) {
            return response(400, "{\"error\":\"" + e.getMessage() + "\"}");
        }

        // Erros de infraestrutura
        catch (InfrastructureException e) {
            return response(500, "{\"error\":\"" + e.getMessage() + "\"}");
        }

        // Erro genérico
        catch (Exception e) {
            return response(500, "{\"error\":\"Erro interno no servidor\"}");
        }
    }

    private APIGatewayProxyResponseEvent handlePost(APIGatewayProxyRequestEvent request) throws Exception {
        if (request.getBody() == null || request.getBody().isBlank()) {
            return response(400, "{\"error\":\"Body não pode ser vazio\"}");
        }

        UrlShortenerDTO dto = mapper.readValue(request.getBody(), UrlShortenerDTO.class);
        String clientId = extrairClientId(request);

        /*
         * AQUI acontece a mágica:
         * se originalUrl NÃO for http ou https,
         * o RegraNegocioValidator vai lançar BusinessException
         * e o handler vai devolver 400 automaticamente.
         */
        UrlShortenerDTO result = service.encurtar(dto.getOriginalUrl(), clientId);

        String domain = request.getHeaders().get("Host");
        result.setShortUrl("https://" + domain + "/" + result.getShortCode());

        return response(201, mapper.writeValueAsString(result));
    }

    private APIGatewayProxyResponseEvent handleGet(APIGatewayProxyRequestEvent request) throws Exception {
        String shortCode = request.getPath()
                .substring(request.getPath().lastIndexOf("/") + 1);

        UrlShortenerDTO result = service.buscarPorShortCode(shortCode);

        APIGatewayProxyResponseEvent redirect = new APIGatewayProxyResponseEvent();
        redirect.setStatusCode(302);
        redirect.setHeaders(Map.of(
                "Location", result.getOriginalUrl(),
                "Access-Control-Allow-Origin", "*"
        ));
        return redirect;
    }

    private String extrairClientId(APIGatewayProxyRequestEvent request) {
        if (request.getHeaders() == null) return null;
        String id = request.getHeaders().get("X-Client-ID");
        return (id != null) ? id : request.getHeaders().get("x-client-id");
    }

    private APIGatewayProxyResponseEvent response(int status, String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(status);
        response.setBody(body);
        response.setHeaders(Map.of(
                "Content-Type", "application/json",
                "Access-Control-Allow-Origin", "*"
        ));
        return response;
    }
}
