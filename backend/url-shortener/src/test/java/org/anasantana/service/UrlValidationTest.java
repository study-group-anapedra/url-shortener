package org.anasantana.service;

import org.anasantana.annotation.utils.validador.Validador;
import org.anasantana.domain.UrlShortener;
import org.anasantana.service.exception.UrlInvalidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UrlValidationTest {

    @Test
    @DisplayName("Deve rejeitar esquemas não permitidos (SSRF) usando o Validador unificado")
    void deveRejeitarEsquemasNaoPermitidos() {
        // 1. Criamos a entidade com o protocolo proibido
        UrlShortener entidade = new UrlShortener();
        entidade.setOriginalUrl("file:///etc/passwd");

        // 2. O Validador deve ler a @ValidarURL e lançar a exceção
        Assertions.assertThrows(UrlInvalidaException.class, () -> {
            Validador.validar(entidade, "usuario-teste");
        }, "O sistema deve bloquear protocolos diferentes de http/https através do Regex da anotação.");
    }

    @Test
    @DisplayName("Deve aceitar URLs válidas com protocolo http ou https")
    void deveAceitarUrlValida() {
        UrlShortener entidade = new UrlShortener();
        entidade.setOriginalUrl("https://google.com");

        Assertions.assertDoesNotThrow(() -> {
            Validador.validar(entidade, "usuario-teste");
        });
    }
}