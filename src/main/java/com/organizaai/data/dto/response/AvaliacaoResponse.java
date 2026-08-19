package com.organizaai.data.dto.response;

import com.organizaai.data.entity.Avaliacao;
import com.organizaai.data.entity.TipoAvaliacao;

import java.time.LocalDate;
import java.util.UUID;

public record AvaliacaoResponse(
        UUID id,
        UUID disciplinaId,
        String disciplinaNome,
        int corIndice,
        String titulo,
        TipoAvaliacao tipo,
        LocalDate data,
        Double pontuacao,
        Double nota
) {
    public static AvaliacaoResponse fromEntity(Avaliacao avaliacao, String disciplinaNome, int corIndice) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getDisciplinaId(),
                disciplinaNome,
                corIndice,
                avaliacao.getTitulo(),
                avaliacao.getTipo(),
                avaliacao.getData(),
                avaliacao.getPontuacao(),
                avaliacao.getNota()
        );
    }
}
