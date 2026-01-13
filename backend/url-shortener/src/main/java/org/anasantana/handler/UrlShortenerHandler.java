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
import org.anasantana.service.exception.InfrastructureException;

import java.util.Map;

public class UrlShortenerHandler implements RequestHandler<
        APIGatewayProxyRequestEvent,
        APIGatewayProxyResponseEvent> {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final UrlShortenerService service;

    public UrlShortenerHandler() {
        EntityManagerSimples entityManager = new EntityManagerSimples();
        UrlShortenerRepository repository = new UrlShortenerRepository(entityManager);
        this.service = new UrlShortenerService(repository);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request,
            Context context) {

        context.getLogger().log("========== NOVA REQUEST ==========\n");

        try {
            String method = request.getHttpMethod();
            String path = request.getPath();

            // 1. CRIAR URL (POST /url)
            if ("POST".equalsIgnoreCase(method) && path != null && path.endsWith("/url")) {
                return handlePost(request, context);
            }

            // 2. REDIRECIONAR (GET /{shortCode})
            // Agora pensado para funcionar no ROOT do domínio, sem /dev
            if ("GET".equalsIgnoreCase(method) && path != null && !path.equals("/")) {
                return handleGet(request, context);
            }

            return response(404, "{\"error\":\"Endpoint não encontrado\"}");

        } catch (InfrastructureException e) {
            context.getLogger().log("ERRO DE INFRA: " + e.getMessage() + "\n");
            return response(500, "{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            context.getLogger().log("ERRO INESPERADO: " + e.getMessage() + "\n");
            return response(500, "{\"error\":\"Erro interno no servidor\"}");
        }
    }

    private APIGatewayProxyResponseEvent handlePost(APIGatewayProxyRequestEvent request, Context context) throws Exception {
        if (request.getBody() == null || request.getBody().isBlank()) {
            return response(400, "{\"error\":\"Body não pode ser vazio\"}");
        }

        UrlShortenerDTO dto = mapper.readValue(request.getBody(), UrlShortenerDTO.class);
        String clientId = extrairClientId(request);
        UrlShortenerDTO result = service.encurtar(dto.getOriginalUrl(), clientId);

        /*
         * A Lambda constrói a URL curta usando exclusivamente o domínio real
         * que chegou na requisição. Não existe hardcode.
         * A infra decide o domínio, a Lambda apenas respeita.
         *
         * Exemplos possíveis:
         *  - 17i0e8ajt5.execute-api.us-east-1.amazonaws.com
         *  - api.asantanadev.com
         */

        String domain = request.getHeaders().get("Host");

        // Agora SEM stage, direto no root:
        // https://api.asantanadev.com/{shortCode}
        String shortUrl = "https://" + domain + "/" + result.getShortCode();
        result.setShortUrl(shortUrl);

        return response(201, mapper.writeValueAsString(result));
    }

    private String extrairClientId(APIGatewayProxyRequestEvent request) {
        if (request.getHeaders() == null) return null;
        String id = request.getHeaders().get("X-Client-ID");
        return (id != null) ? id : request.getHeaders().get("x-client-id");
    }

    private APIGatewayProxyResponseEvent handleGet(APIGatewayProxyRequestEvent request, Context context) throws Exception {
        String path = request.getPath();
        // Extrai o código final da URL (ex: de /6f9e5a95 extrai 6f9e5a95)
        String shortCode = path.substring(path.lastIndexOf("/") + 1);

        UrlShortenerDTO result = service.buscarPorShortCode(shortCode);

        // Redirecionamento real via HTTP 302
        APIGatewayProxyResponseEvent redirectResponse = new APIGatewayProxyResponseEvent();
        redirectResponse.setStatusCode(302);
        redirectResponse.setHeaders(Map.of(
                "Location", result.getOriginalUrl(),
                "Access-Control-Allow-Origin", "*"
        ));
        return redirectResponse;
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
