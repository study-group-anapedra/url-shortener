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
import java.util.Locale;
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
            final String method = safeUpper(request.getHttpMethod());
            final String path = request.getPath() != null ? request.getPath() : "";

            // ===== CORS PRE-FLIGHT =====
            if ("OPTIONS".equals(method)) {
                return response(200, "");
            }

            // ===== POST /url =====
            if ("POST".equals(method) && path.endsWith("/url")) {
                return handlePost(request);
            }

            // ===== GET /{shortCode} =====
            if ("GET".equals(method) && !"/".equals(path)) {
                return handleGet(request);
            }

            return response(404, jsonError("Endpoint não encontrado"));

        } catch (BusinessException e) {
            return response(400, jsonError(e.getMessage()));
        } catch (AbusoDeRequisicaoException e) {
            return response(429, jsonError("Muitas requisições"));
        } catch (UrlNaoEncontradaException e) {
            return response(404, jsonError("URL não encontrada"));
        } catch (Exception e) {
            context.getLogger().log("ERRO: " + e.getMessage());
            return response(500, jsonError("Erro interno"));
        }
    }

    private APIGatewayProxyResponseEvent handlePost(APIGatewayProxyRequestEvent request) throws Exception {

        if (request.getBody() == null || request.getBody().isBlank()) {
            return response(400, jsonError("Body vazio"));
        }

        UrlShortenerDTO dto = mapper.readValue(request.getBody(), UrlShortenerDTO.class);

        String clientId = extrairClientId(request);
        UrlShortenerDTO result = service.encurtar(dto, clientId);

        String domain = resolveHost(request);
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

        String id = getHeaderIgnoreCase(request.getHeaders(), "X-Client-ID");
        if (id != null && !id.isBlank()) return id;

        if (request.getRequestContext() != null &&
                request.getRequestContext().getIdentity() != null) {
            String ip = request.getRequestContext().getIdentity().getSourceIp();
            if (ip != null) return ip;
        }
        return "anonymous";
    }

    private String resolveHost(APIGatewayProxyRequestEvent request) {
        if (request.getHeaders() == null) return "api.asantanadev.com";
        String host = getHeaderIgnoreCase(request.getHeaders(), "Host");
        return (host != null && !host.isBlank()) ? host : "api.asantanadev.com";
    }

    private Map<String, String> corsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Client-ID");
        headers.put("Content-Type", "application/json");
        return headers;
    }

    private APIGatewayProxyResponseEvent response(int status, String body) {

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status)
                .withHeaders(corsHeaders())
                .withBody(body);
    }

    private String jsonError(String msg) {
        return "{\"error\":\"" + msg.replace("\"", "'") + "\"}";
    }

    private String getHeaderIgnoreCase(Map<String, String> headers, String key) {
        for (Map.Entry<String, String> e : headers.entrySet()) {
            if (e.getKey() != null && e.getKey().equalsIgnoreCase(key)) {
                return e.getValue();
            }
        }
        return null;
    }

    private String safeUpper(String s) {
        return s == null ? "" : s.toUpperCase(Locale.ROOT);
    }
}