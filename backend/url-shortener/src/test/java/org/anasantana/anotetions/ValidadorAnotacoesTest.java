package org.anasantana.anotetions;

import org.anasantana.annotation.RateLimited;
import org.anasantana.annotation.ValidarURL;
import org.anasantana.annotation.utils.validador.Validador;
import org.anasantana.service.exception.AbusoDeRequisicaoException;
import org.anasantana.service.exception.UrlInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorAnotacoesTest {

    // 1. Classe de teste para @RateLimited (Limite de 2 requisições por 10 segundos)
    @RateLimited(limite = 2, janelaSegundos = 10)
    static class EntidadeProtegida {
        @ValidarURL
        String url;

        EntidadeProtegida(String url) { this.url = url; }
    }

    // --- TESTE DA ANOTAÇÃO @RateLimited ---
    @Test
    @DisplayName("Deve bloquear o usuário após exceder o limite de requisições configurado")
    void deveTestarRateLimit() throws IllegalAccessException {
        EntidadeProtegida objeto = new EntidadeProtegida("https://google.com");
        String usuarioId = "usuario-teste-rate";

        // Primeira e Segunda tentativas: Devem passar
        assertDoesNotThrow(() -> Validador.validar(objeto, usuarioId));
        assertDoesNotThrow(() -> Validador.validar(objeto, usuarioId));

        // Terceira tentativa: Deve lançar AbusoDeRequisicaoException
        assertThrows(AbusoDeRequisicaoException.class, () -> {
            Validador.validar(objeto, usuarioId);
        }, "O Rate Limit deveria ter bloqueado o usuário no 3º acesso.");
    }

    // --- TESTE DA ANOTAÇÃO @ValidarURL ---
    @Test
    @DisplayName("Deve lançar exceção quando a URL não seguir o padrão ou tamanho permitido")
    void deveTestarValidacaoDeURL() {
        String usuarioId = "usuario-url-teste";

        // Caso 1: URL Malformada (sem protocolo correto)
        EntidadeProtegida urlRuim = new EntidadeProtegida("ftp://site.com");
        assertThrows(UrlInvalidaException.class, () -> {
            Validador.validar(urlRuim, usuarioId);
        }, "Deveria rejeitar protocolos diferentes de http/https");

        // Caso 2: URL Vazia ou Nula
        EntidadeProtegida urlVazia = new EntidadeProtegida("");
        assertThrows(UrlInvalidaException.class, () -> {
            Validador.validar(urlVazia, usuarioId);
        });

    }

    @Test
    @DisplayName("Caso 3: Deve aceitar URL válida quando o limite de acessos não foi atingido")
    void deveAceitarUrlValida() {
        // Usamos um ID novo para garantir que o contador de Rate Limit esteja zerado
        String novoUsuarioId = "usuario-valido-teste";
        EntidadeProtegida urlBoa = new EntidadeProtegida("https://github.com/ana-santana");

        // Não deve lançar nenhuma exceção (nem de URL, nem de Rate Limit)
        assertDoesNotThrow(() -> Validador.validar(urlBoa, novoUsuarioId),
                "A URL deveria ser considerada válida e o acesso permitido.");
    }
}