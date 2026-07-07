package com.organizaai.data.dto.response;

import com.organizaai.data.entity.Disciplina;

import java.time.Instant;
import java.util.UUID;

public record DisciplinaResponse(
        UUID id,
        String nome,
        int creditos,
        int faltas,
        Instant createdAt
) {
    public static DisciplinaResponse fromEntity(Disciplina disciplina, long quantidadeBlocos) {
        return new DisciplinaResponse(
                disciplina.getId(),
                disciplina.getNome(),
                (int) (quantidadeBlocos * 2),
                disciplina.getFaltas(),
                disciplina.getCreatedAt()
        );
    }
}
