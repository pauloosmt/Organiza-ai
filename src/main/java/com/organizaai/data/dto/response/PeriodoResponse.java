package com.organizaai.data.dto.response;

import com.organizaai.data.entity.Periodo;

import java.time.Instant;
import java.util.UUID;

public record PeriodoResponse(
        UUID id,
        String nome,
        double escalaTotal,
        double mediaMinimaPassar,
        double mediaMinimaRecuperacao,
        Instant createdAt
) {
    public static PeriodoResponse fromEntity(Periodo periodo) {
        return new PeriodoResponse(
                periodo.getId(),
                periodo.getNome(),
                periodo.getEscalaTotal(),
                periodo.getMediaMinimaPassar(),
                periodo.getMediaMinimaRecuperacao(),
                periodo.getCreatedAt()
        );
    }
}
