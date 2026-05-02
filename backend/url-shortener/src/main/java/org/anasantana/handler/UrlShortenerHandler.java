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

import java.util.HashMap;
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

            //CORS PRE-FLIGHT
            if ("OPTIONS".equalsIgnoreCase(method)) {
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(200)
                        .withHeaders(corsHeaders())
                        .withBody("");
            }

            if ("POST".equalsIgnoreCase(method) && path != null && path.endsWith("/url")) {
                return handlePost(request);
            }

            if ("GET".equalsIgnoreCase(method) && path != null && !path.equals("/")) {
                return handleGet(request);
            }

            return response(404, "{\"error\":\"Endpoint não encontrado\"}");

        } catch (BusinessException e) {
            return response(400, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (AbusoDeRequisicaoException e) {
            return response(429, "{\"error\":\"Muitas requisições\"}");
        } catch (UrlNaoEncontradaException e) {
            return response(404, "{\"error\":\"URL não encontrada\"}");
        } catch (Exception e) {
            context.getLogger().log("ERRO: " + e.getMessage());
            return response(500, "{\"error\":\"Erro interno\"}");
        }
    }

    private APIGatewayProxyResponseEvent handlePost(APIGatewayProxyRequestEvent request) throws Exception {
        UrlShortenerDTO dto = mapper.readValue(request.getBody(), UrlShortenerDTO.class);

        String clientId = extrairClientId(request);
        UrlShortenerDTO result = service.encurtar(dto, clientId);

        String domain = request.getHeaders() != null
                ? request.getHeaders().getOrDefault("Host", "api.asantanadev.com")
                : "api.asantanadev.com";

        result.setShortUrl("https://" + domain + "/" + result.getShortCode());

        return response(201, mapper.writeValueAsString(result));
    }

    private APIGatewayProxyResponseEvent handleGet(APIGatewayProxyRequestEvent request) throws Exception {
        String path = request.getPath();
        String shortCode = path.substring(path.lastIndexOf("/") + 1);

        UrlShortenerDTO result = service.buscarPorShortCode(shortCode);

        Map<String, String> headers = corsHeaders();
        headers.put("Location", result.getOriginalUrl());

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(302)
                .withHeaders(headers)
                .withBody("");
    }

    private String extrairClientId(APIGatewayProxyRequestEvent request) {
        if (request.getHeaders() == null) return "anonymous";

        String id = request.getHeaders().get("X-Client-ID");
        if (id == null) id = request.getHeaders().get("x-client-id");

        return (id != null) ? id : request.getRequestContext().getIdentity().getSourceIp();
    }

    private Map<String, String> corsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Client-ID");
        return headers;
    }

    private APIGatewayProxyResponseEvent response(int status, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status)
                .withHeaders(corsHeaders())
                .withBody(body);
    }
}