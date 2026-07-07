package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreatePeriodoRequest(

        @NotNull
        @Min(2000)
        @Max(2100)
        Integer ano,

        @NotNull
        @Min(1)
        @Max(2)
        Integer semestre
) {
}
