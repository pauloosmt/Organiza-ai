package com.organizaai.exceptions.entity;

public class SenhaAtualInvalidaException extends RuntimeException {

    public SenhaAtualInvalidaException() {
        super("Senha atual incorreta");
    }
}
