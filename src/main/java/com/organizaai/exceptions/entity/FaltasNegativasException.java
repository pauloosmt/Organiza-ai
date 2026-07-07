package com.organizaai.exceptions.entity;

public class FaltasNegativasException extends RuntimeException {

    public FaltasNegativasException() {
        super("O número de faltas não pode ficar negativo.");
    }
}
