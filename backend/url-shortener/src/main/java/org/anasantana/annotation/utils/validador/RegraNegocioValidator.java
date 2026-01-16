package org.anasantana.annotation.utils.validador;

import org.anasantana.annotation.GerarCodigo;
import org.anasantana.annotation.RateLimited;
import org.anasantana.annotation.ValidarURL;
import org.anasantana.service.exception.AbusoDeRequisicaoException;
import org.anasantana.service.exception.BusinessException;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class RegraNegocioValidator {

    private static final Map<String, Janela> controleRateLimit = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    private RegraNegocioValidator() {
        // impede instanciação
    }

    public static void validar(Object objeto, String identificadorUsuario) throws IllegalAccessException {
        if (objeto == null) {
            return;
        }

        Class<?> clazz = objeto.getClass();

        // 1. RATE LIMIT (nível de classe)
        if (clazz.isAnnotationPresent(RateLimited.class)) {
            processarRateLimit(identificadorUsuario, clazz.getAnnotation(RateLimited.class));
        }

        // 2. VALIDAÇÕES E GERAÇÕES POR CAMPO
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object valor = field.get(objeto);

            // 2.1 Geração automática de código
            if (field.isAnnotationPresent(GerarCodigo.class)
                    && (valor == null || valor.toString().isEmpty())) {

                GerarCodigo config = field.getAnnotation(GerarCodigo.class);
                String novoCodigo = gerarRandomico(config.tamanho(), config.alfabeto());
                field.set(objeto, novoCodigo);
                valor = novoCodigo;
            }

            // 2.2 Validação de URL
            if (field.isAnnotationPresent(ValidarURL.class)) {
                ValidarURL anotacao = field.getAnnotation(ValidarURL.class);

                String url = (valor != null) ? valor.toString() : "";

                if (url.isEmpty()
                        || url.length() > 2048
                        || !Pattern.compile(anotacao.regex()).matcher(url).matches()) {

                    throw new BusinessException(anotacao.mensagem());
                }
            }
        }
    }

    // ===== Métodos auxiliares =====

    private static String gerarRandomico(int tamanho, String alfabeto) {
        StringBuilder sb = new StringBuilder(tamanho);
        for (int i = 0; i < tamanho; i++) {
            sb.append(alfabeto.charAt(RANDOM.nextInt(alfabeto.length())));
        }
        return sb.toString();
    }

    private static void processarRateLimit(String chave, RateLimited config) {
        long agora = Instant.now().getEpochSecond();

        controleRateLimit.compute(chave, (k, janela) -> {
            if (janela == null || agora - janela.inicio >= config.janelaSegundos()) {
                return new Janela(1, agora);
            }

            if (janela.contador >= config.limite()) {
                throw new AbusoDeRequisicaoException();
            }

            janela.contador++;
            return janela;
        });
    }

    private static class Janela {
        int contador;
        long inicio;

        Janela(int contador, long inicio) {
            this.contador = contador;
            this.inicio = inicio;
        }
    }
}
