package com.organizaai.exceptions.entity;

public class PeriodoJaExisteException extends RuntimeException {

    public PeriodoJaExisteException(String nome) {
        super("Já existe um período com o nome: " + nome);
    }
}
