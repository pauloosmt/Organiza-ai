package com.organizaai.service;

import com.organizaai.data.entity.Periodo;
import com.organizaai.exceptions.entity.ConfigPeriodoInvalidaException;
import com.organizaai.exceptions.entity.PeriodoJaExisteException;
import com.organizaai.exceptions.entity.PeriodoNotFoundException;
import com.organizaai.repository.PeriodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PeriodoService {

    private final PeriodoRepository periodoRepository;

    public Periodo create(UUID userId, int ano, int semestre) {
        String nome = ano + "/" + semestre;
        if (periodoRepository.findByUserIdAndNome(userId, nome).isPresent()) {
            throw new PeriodoJaExisteException(nome);
        }
        return periodoRepository.save(new Periodo(userId, nome));
    }

    public Periodo atualizarConfig(UUID userId, UUID periodoId, double escalaTotal, double mediaMinimaPassar, double mediaMinimaRecuperacao) {
        if (mediaMinimaRecuperacao < 0 || mediaMinimaRecuperacao > mediaMinimaPassar || mediaMinimaPassar > escalaTotal) {
            throw new ConfigPeriodoInvalidaException();
        }
        Periodo periodo = periodoRepository.findByIdAndUserId(periodoId, userId)
                .orElseThrow(() -> new PeriodoNotFoundException(periodoId));
        periodo.setEscalaTotal(escalaTotal);
        periodo.setMediaMinimaPassar(mediaMinimaPassar);
        periodo.setMediaMinimaRecuperacao(mediaMinimaRecuperacao);
        return periodoRepository.save(periodo);
    }

    public List<Periodo> list(UUID userId) {
        return periodoRepository.findByUserIdOrderByNomeDesc(userId);
    }

    public Periodo findOrCreate(UUID userId, String nome) {
        return periodoRepository.findByUserIdAndNome(userId, nome)
                .orElseGet(() -> periodoRepository.save(new Periodo(userId, nome)));
    }
}
