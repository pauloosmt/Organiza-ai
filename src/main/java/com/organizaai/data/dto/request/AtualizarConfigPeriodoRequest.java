package com.organizaai.data.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AtualizarConfigPeriodoRequest(

        @NotNull
        @PositiveOrZero
        Double escalaTotal,

        @NotNull
        @PositiveOrZero
        Double mediaMinimaPassar,

        @NotNull
        @PositiveOrZero
        Double mediaMinimaRecuperacao
) {
}
