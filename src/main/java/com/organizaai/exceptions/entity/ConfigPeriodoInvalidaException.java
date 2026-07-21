package com.organizaai.exceptions.entity;

public class ConfigPeriodoInvalidaException extends RuntimeException {

    public ConfigPeriodoInvalidaException() {
        super("Configuração inválida: a ordem deve ser 0 <= média mínima de recuperação <= média mínima pra passar <= escala total.");
    }
}
