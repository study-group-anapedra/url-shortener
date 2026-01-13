package org.anasantana.service.exception;

public class UrlNaoEncontradaException extends RuntimeException {
    public UrlNaoEncontradaException() {
        super("URL não encontrada.");
    }
}
