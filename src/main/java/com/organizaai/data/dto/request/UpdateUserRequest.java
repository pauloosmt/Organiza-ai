package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        String name,

        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String password
) {
}
