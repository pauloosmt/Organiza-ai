package com.organizaai.data.dto.request;

import jakarta.validation.constraints.NotNull;

public record AjustarFaltasRequest(

        @NotNull
        Integer delta
) {
}
