package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ReenviarCodigoRequest(

        @NotBlank
        @Email
        String email
) {
}
