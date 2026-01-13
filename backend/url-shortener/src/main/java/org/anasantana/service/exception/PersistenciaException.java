package org.anasantana.service.exception;

// repository.exception
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
