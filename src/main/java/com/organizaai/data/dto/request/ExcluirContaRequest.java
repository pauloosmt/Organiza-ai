package com.organizaai.data.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExcluirContaRequest(

        @NotBlank
        String senha
) {
}
