package com.organizaai.exceptions.entity;

public class PontuacaoObrigatoriaException extends RuntimeException {

    public PontuacaoObrigatoriaException() {
        super("Pontuação é obrigatória para esse tipo de avaliação.");
    }
}
