package org.anasantana.service;

import org.anasantana.annotation.utils.validador.RegraNegocioValidator;
import org.anasantana.domain.UrlShortener;
import org.anasantana.dto.UrlShortenerDTO;
import org.anasantana.service.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UrlValidationTest {
    @Test
    @DisplayName("Deve rejeitar esquemas não permitidos (SSRF) usando o Validador unificado")
    void deveRejeitarEsquemasNaoPermitidos() throws IllegalAccessException {
        // 1. Criamos o DTO com o protocolo proibido
        UrlShortenerDTO dto = new UrlShortenerDTO();
        dto.setOriginalUrl("file:///etc/passwd");

        // 2. O Validador deve ler a @ValidarURL e lançar BusinessException
        BusinessException ex = Assertions.assertThrows(
                BusinessException.class,
                () -> RegraNegocioValidator.validar(dto, "usuario-teste"),
                "O sistema deve bloquear protocolos diferentes de http/https através do Regex da anotação."
        );

        // 3. Conferimos a mensagem definida na annotation
        Assertions.assertEquals(
                "Apenas URLs com http ou https são permitidas",
                ex.getMessage()
        );
    }


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