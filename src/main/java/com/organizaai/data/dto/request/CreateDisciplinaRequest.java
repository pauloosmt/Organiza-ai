package com.organizaai.data.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDisciplinaRequest(

        @NotBlank
        String nome
) {
}
