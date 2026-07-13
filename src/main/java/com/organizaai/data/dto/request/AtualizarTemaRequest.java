package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Pattern;

public record AtualizarTemaRequest(

        @Pattern(regexp = "light|dark", message = "Tema deve ser 'light' ou 'dark'")
        String tema
) {
}
