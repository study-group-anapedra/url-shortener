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
//
//public class Validador {
//
//    private static final Map<String, Janela> controleRateLimit = new ConcurrentHashMap<>();
//    private static final SecureRandom RANDOM = new SecureRandom();
//
//    public static void validar(Object objeto, String idUsuario) throws IllegalAccessException {
//        if (objeto == null) return;
//
//        Class<?> clazz = objeto.getClass();
//
//        if (clazz.isAnnotationPresent(RateLimited.class)) {
//            processarRateLimit(idUsuario, clazz.getAnnotation(RateLimited.class));
//        }
//
//        for (Field field : clazz.getDeclaredFields()) {
//            field.setAccessible(true);
//            Object valor = field.get(objeto);
//
//            if (field.isAnnotationPresent(GerarCodigo.class) && (valor == null || valor.toString().isEmpty())) {
//                GerarCodigo config = field.getAnnotation(GerarCodigo.class);
//                String codigoGerado = gerarTexto(config.tamanho(), config.alfabeto());
//                field.set(objeto, codigoGerado);
//                valor = codigoGerado;
//            }
//
//            if (field.isAnnotationPresent(ValidarURL.class)) {
//                ValidarURL anotacao = field.getAnnotation(ValidarURL.class);
//                String url = (valor != null) ? valor.toString() : "";
//
//                if (url.length() > 2048 || url.isEmpty() || !Pattern.compile(anotacao.regex()).matcher(url).matches()) {
//                    throw new UrlInvalidaException("URL inválida ou protocolo não permitido.");
//                }
//            }
//        }
//    }
//
//    private static String gerarTexto(int tamanho, String alfabeto) {
//        StringBuilder sb = new StringBuilder(tamanho);
//        for (int i = 0; i < tamanho; i++) {
//            sb.append(alfabeto.charAt(RANDOM.nextInt(alfabeto.length())));
//        }
//        return sb.toString();
//    }
//
//    private static void processarRateLimit(String chave, RateLimited config) {
//        long agora = Instant.now().getEpochSecond();
//        controleRateLimit.compute(chave, (k, j) -> {
//            if (j == null || agora - j.inicio >= config.janelaSegundos()) return new Janela(1, agora);
//            if (j.contador >= config.limite()) throw new AbusoDeRequisicaoException();
//            j.contador++;
//            return j;
//        });
//    }
//
//    private static class Janela {
//        int contador; long inicio;
//        Janela(int c, long i) { this.contador = c; this.inicio = i; }
//    }
//}