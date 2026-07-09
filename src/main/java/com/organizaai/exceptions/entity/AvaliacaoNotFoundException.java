package com.organizaai.exceptions.entity;

import java.util.UUID;

public class AvaliacaoNotFoundException extends RuntimeException {

    public AvaliacaoNotFoundException(UUID id) {
        super("Avaliação não encontrada: " + id);
    }
}
