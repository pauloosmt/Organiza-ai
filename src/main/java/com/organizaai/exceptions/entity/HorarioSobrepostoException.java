package com.organizaai.exceptions.entity;

public class HorarioSobrepostoException extends RuntimeException {

    public HorarioSobrepostoException() {
        super("Já existe uma disciplina alocada nesse horário.");
    }
}
