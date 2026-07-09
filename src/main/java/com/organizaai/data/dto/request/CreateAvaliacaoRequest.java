package com.organizaai.data.dto.request;

import com.organizaai.data.entity.TipoAvaliacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAvaliacaoRequest(

        @NotNull
        UUID disciplinaId,

        @NotBlank
        String titulo,

        @NotNull
        TipoAvaliacao tipo,

        @NotNull
        LocalDate data,

        @NotNull
        @Positive
        Double pontuacao
) {
}
