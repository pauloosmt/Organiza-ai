package com.organizaai.exceptions.entity;

import java.util.UUID;

public class DisciplinaNotFoundException extends RuntimeException {

    public DisciplinaNotFoundException(UUID id) {
        super("Disciplina não encontrada: " + id);
    }
}
