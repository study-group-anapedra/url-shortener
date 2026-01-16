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
import org.anasantana.service.exception.*;

import java.util.Map;

public class UrlShortenerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final UrlShortenerService service;

    public UrlShortenerHandler() {
        EntityManagerSimples entityManager = new EntityManagerSimples();
        UrlShortenerRepository repository = new UrlShortenerRepository(entityManager);
        this.service = new UrlShortenerService(repository);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String method = request.getHttpMethod();
            String path = request.getPath();

            if ("POST".equalsIgnoreCase(method) && path != null && path.endsWith("/url")) {
                return handlePost(request, context);
            }
            if ("GET".equalsIgnoreCase(method) && path != null && !path.equals("/")) {
                return handleGet(request, context);
            }
            return response(404, "{\"error\":\"Endpoint não encontrado\"}");

        } catch (BusinessException e) {
            return response(400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (AbusoDeRequisicaoException e) {
            return response(429, "{\"error\":\"Muitas requisições. Tente mais tarde.\"}");
        } catch (UrlNaoEncontradaException e) {
            return response(404, "{\"error\":\"URL não encontrada\"}");
        } catch (InfrastructureException e) {
            context.getLogger().log("ERRO DE INFRA: " + e.getMessage());
            return response(500, "{\"error\":\"Falha na infraestrutura\"}");
        } catch (Exception e) {
            context.getLogger().log("ERRO INESPERADO: " + e.getMessage());
            return response(500, "{\"error\":\"Erro interno no servidor\"}");
        }
    }

    private APIGatewayProxyResponseEvent handlePost(APIGatewayProxyRequestEvent request, Context context) throws Exception {
        if (request.getBody() == null || request.getBody().isBlank()) {
            return response(400, "{\"error\":\"Body não pode ser vazio\"}");
        }

        UrlShortenerDTO dto = mapper.readValue(request.getBody(), UrlShortenerDTO.class);
        String clientId = extrairClientId(request);
        UrlShortenerDTO result = service.encurtar(dto, clientId);

        String domain = request.getHeaders().getOrDefault("Host", "api.asantanadev.com");
        result.setShortUrl("https://" + domain + "/" + result.getShortCode());

        return response(201, mapper.writeValueAsString(result));
    }

    private String extrairClientId(APIGatewayProxyRequestEvent request) {
        if (request.getHeaders() == null) return "anonymous";
        String id = request.getHeaders().get("X-Client-ID");
        if (id == null) id = request.getHeaders().get("x-client-id");
        return (id != null) ? id : request.getRequestContext().getIdentity().getSourceIp();
    }

    private APIGatewayProxyResponseEvent handleGet(APIGatewayProxyRequestEvent request, Context context) throws Exception {
        String path = request.getPath();
        String shortCode = path.substring(path.lastIndexOf("/") + 1);
        UrlShortenerDTO result = service.buscarPorShortCode(shortCode);

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(302)
                .withHeaders(Map.of("Location", result.getOriginalUrl(), "Access-Control-Allow-Origin", "*"));
    }

    private APIGatewayProxyResponseEvent response(int status, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status)
                .withBody(body)
                .withHeaders(Map.of("Content-Type", "application/json", "Access-Control-Allow-Origin", "*"));
    }
}