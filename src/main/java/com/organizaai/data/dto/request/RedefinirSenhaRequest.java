package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String codigo,

        @NotBlank
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String novaSenha
) {
}
