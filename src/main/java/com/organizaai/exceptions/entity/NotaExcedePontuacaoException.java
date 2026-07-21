package com.organizaai.exceptions.entity;

public class NotaExcedePontuacaoException extends RuntimeException {

    public NotaExcedePontuacaoException(double pontuacaoMaxima) {
        super("Nota não pode ultrapassar a pontuação máxima da avaliação (" + pontuacaoMaxima + ").");
    }
}
