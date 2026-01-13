package org.anasantana.service.exception;

public class CodigoCurtoNaoDisponivelException extends RuntimeException {
    public CodigoCurtoNaoDisponivelException() {
        super("Não foi possível gerar um código curto único.");
    }
}
