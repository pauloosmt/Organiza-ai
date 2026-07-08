package com.organizaai.data.dto.response;

import com.organizaai.data.entity.GradeBloco;

import java.util.UUID;

public record GradeBlocoResponse(
        UUID id,
        UUID disciplinaId,
        String disciplinaNome,
        int corIndice,
        int diaSemana,
        int horaInicio,
        int horaFim
) {
    public static GradeBlocoResponse fromEntity(GradeBloco bloco, String disciplinaNome, int corIndice) {
        return new GradeBlocoResponse(
                bloco.getId(),
                bloco.getDisciplinaId(),
                disciplinaNome,
                corIndice,
                bloco.getDiaSemana(),
                bloco.getHoraInicio(),
                bloco.getHoraFim()
        );
    }
}
