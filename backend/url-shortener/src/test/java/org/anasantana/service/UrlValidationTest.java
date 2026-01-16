package org.anasantana.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.anasantana.annotation.utils.validador.RegraNegocioValidator;
import org.anasantana.domain.UrlShortener;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.handler.TestContext;
import org.anasantana.handler.UrlShortenerHandler;
import org.anasantana.service.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidationTest {







    @Test
    @DisplayName("Deve aceitar URLs válidas com protocolo http ou https")
    void deveAceitarUrlValida() {
        UrlShortener entidade = new UrlShortener();
        entidade.setOriginalUrl("https://google.com");

        Assertions.assertDoesNotThrow(() -> {
            RegraNegocioValidator.validar(entidade, "usuario-teste");
        });
    }
}