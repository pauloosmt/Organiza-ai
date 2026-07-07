package com.organizaai.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateDisciplinaRequest(

        @NotBlank
        String nome,

        @NotNull
        UUID periodoId
) {
}
