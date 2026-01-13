package org.anasantana.service.exception;

/**
 * Erro para falhas técnicas (Docker, Banco, Rede).
 */
public class InfrastructureException extends RuntimeException {
    public InfrastructureException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}