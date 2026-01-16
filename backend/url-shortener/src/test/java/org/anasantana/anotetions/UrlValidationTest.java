package org.anasantana.anotetions;

import org.anasantana.annotation.ValidarURL;
import org.anasantana.annotation.utils.validador.RegraNegocioValidator;
import org.anasantana.service.exception.BusinessException;
import org.anasantana.service.exception.UrlInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UrlValidationTest {

    static class ExemploUrl {
        @ValidarURL
        private String url;

        public ExemploUrl(String url) { 
            this.url = url; 
        }
    }

    @Test
    @DisplayName("Deve aceitar uma URL válida (HTTPS)")
    void deveAceitarUrlValida() {
        ExemploUrl exemplo = new ExemploUrl("https://www.google.com");
        
        assertDoesNotThrow(() -> {
            RegraNegocioValidator.validar(exemplo, "user123");
        });
    }

    @Test
    @DisplayName("Deve lançar BusinessException para URL mal formatada")
    void deveRejeitarUrlInvalida() {
        ExemploUrl exemplo = new ExemploUrl("url-invalida");

        assertThrows(BusinessException.class, () -> {
            RegraNegocioValidator.validar(exemplo, "user123");
        });
    }
}