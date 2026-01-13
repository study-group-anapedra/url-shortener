package org.anasantana.service.exception;

/**
 * Erro específico para regras de negócio (ex: URL duplicada).
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}