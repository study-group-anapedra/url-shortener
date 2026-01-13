package org.anasantana.annotation.utils.validador;

import org.anasantana.annotation.GerarCodigo;
import org.anasantana.annotation.RateLimited;
import org.anasantana.annotation.ValidarURL;
import org.anasantana.service.exception.AbusoDeRequisicaoException;
import org.anasantana.service.exception.UrlInvalidaException;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class RegraNegocioValidator {

    private static final Map<String, Janela> controleRateLimit = new ConcurrentHashMap<>();
    private static final SecureRandom RANDOM = new SecureRandom();

    public static void validar(Object objeto, String identificadorUsuario) throws IllegalAccessException {
        Class<?> clazz = objeto.getClass();

        // 1. Rate Limit
        if (clazz.isAnnotationPresent(RateLimited.class)) {
            processarRateLimit(identificadorUsuario, clazz.getAnnotation(RateLimited.class));
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object valor = field.get(objeto);

            // 2. GERAÇÃO DE CÓDIGO (Substitui a lógica de Código Curto que dava erro)
            if (field.isAnnotationPresent(GerarCodigo.class) && (valor == null || valor.toString().isEmpty())) {
                GerarCodigo config = field.getAnnotation(GerarCodigo.class);
                String novoCodigo = gerarRandomico(config.tamanho(), config.alfabeto());
                field.set(objeto, novoCodigo);
                valor = novoCodigo; 
            }

            // 3. VALIDAÇÃO DE URL
            if (field.isAnnotationPresent(ValidarURL.class) && valor instanceof String url) {
                ValidarURL anotacao = field.getAnnotation(ValidarURL.class);
                if (url.length() > 2048 || !Pattern.compile(anotacao.regex()).matcher(url).matches()) {
                    throw new UrlInvalidaException("URL inválida ou protocolo não permitido.");
                }
            }
        }
    }

    private static String gerarRandomico(int tam, String alfabeto) {
        StringBuilder sb = new StringBuilder(tam);
        for (int i = 0; i < tam; i++) {
            sb.append(alfabeto.charAt(RANDOM.nextInt(alfabeto.length())));
        }
        return sb.toString();
    }

    private static void processarRateLimit(String chave, RateLimited config) {
        long agora = Instant.now().getEpochSecond();
        controleRateLimit.compute(chave, (k, j) -> {
            if (j == null || agora - j.inicio >= config.janelaSegundos()) return new Janela(1, agora);
            if (j.contador >= config.limite()) throw new AbusoDeRequisicaoException();
            j.contador++; return j;
        });
    }

    private static class Janela {
        int contador; long inicio;
        Janela(int c, long i) { this.contador = c; this.inicio = i; }
    }
}