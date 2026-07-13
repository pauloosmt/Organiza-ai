package com.organizaai.data.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ConfirmarTrocaSenhaRequest(

        @NotBlank
        String codigo
) {
}
