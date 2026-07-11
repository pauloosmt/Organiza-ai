package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificarEmailRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String codigo
) {
}
