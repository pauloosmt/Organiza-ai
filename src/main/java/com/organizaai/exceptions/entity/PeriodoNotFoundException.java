package com.organizaai.exceptions.entity;

import java.util.UUID;

public class PeriodoNotFoundException extends RuntimeException {

    public PeriodoNotFoundException(UUID id) {
        super("Período não encontrado: " + id);
    }
}
