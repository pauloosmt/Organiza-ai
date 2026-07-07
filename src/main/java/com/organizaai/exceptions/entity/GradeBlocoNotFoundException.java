package com.organizaai.exceptions.entity;

import java.util.UUID;

public class GradeBlocoNotFoundException extends RuntimeException {

    public GradeBlocoNotFoundException(UUID id) {
        super("Bloco de grade não encontrado: " + id);
    }
}
