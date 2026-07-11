package com.organizaai.exceptions.entity;

public class CodigoVerificacaoInvalidoException extends RuntimeException {

    public CodigoVerificacaoInvalidoException() {
        super("Código de verificação inválido ou expirado");
    }
}
