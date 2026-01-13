package org.anasantana.service.exception;

public class AbusoDeRequisicaoException extends RuntimeException {
    public AbusoDeRequisicaoException() {
        super("Muitas requisições em curto período.");
    }
}
