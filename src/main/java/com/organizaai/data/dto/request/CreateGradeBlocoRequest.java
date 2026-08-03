package com.organizaai.data.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateGradeBlocoRequest(

        @NotNull
        UUID disciplinaId,

        @NotNull
        @Min(1)
        @Max(6)
        Integer diaSemana,

        @NotNull
        @Min(6)
        @Max(22)
        Integer horaInicio,

        @NotNull
        @Min(7)
        @Max(23)
        Integer horaFim,

        @Size(max = 80)
        String sala
) {
}
