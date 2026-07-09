package com.organizaai.data.dto.request;

import com.organizaai.data.entity.TipoAvaliacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record UpdateAvaliacaoRequest(

        @NotBlank
        String titulo,

        @NotNull
        TipoAvaliacao tipo,

        @NotNull
        LocalDate data,

        @NotNull
        @Positive
        Double pontuacao,

        @PositiveOrZero
        Double nota
) {
}
