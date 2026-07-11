package com.organizaai.exceptions.login;

public class EmailNaoVerificadoException extends RuntimeException {

    public EmailNaoVerificadoException() {
        super("Verifique seu email antes de entrar");
    }
}
