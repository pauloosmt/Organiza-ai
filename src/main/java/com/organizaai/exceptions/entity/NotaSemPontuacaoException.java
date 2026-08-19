package com.organizaai.exceptions.entity;

public class NotaSemPontuacaoException extends RuntimeException {

    public NotaSemPontuacaoException() {
        super("Não é possível lançar nota numa avaliação sem pontuação definida.");
    }
}
