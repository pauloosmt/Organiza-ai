package com.organizaai.infra.migration;

import com.organizaai.data.entity.Disciplina;
import com.organizaai.data.entity.Periodo;
import com.organizaai.repository.DisciplinaRepository;
import com.organizaai.service.PeriodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PeriodoMigrationRunner implements ApplicationRunner {

    private static final String PERIODO_MIGRACAO = "2026/2";

    private final DisciplinaRepository disciplinaRepository;
    private final PeriodoService periodoService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Disciplina> orfas = disciplinaRepository.findByPeriodoIdIsNull();
        if (orfas.isEmpty()) {
            return;
        }

        Map<UUID, List<Disciplina>> orfasPorUsuario = orfas.stream()
                .collect(Collectors.groupingBy(Disciplina::getUserId));

        orfasPorUsuario.forEach((userId, disciplinasDoUsuario) -> {
            Periodo periodo = periodoService.findOrCreate(userId, PERIODO_MIGRACAO);
            disciplinasDoUsuario.forEach(d -> d.setPeriodoId(periodo.getId()));
            disciplinaRepository.saveAll(disciplinasDoUsuario);
        });
    }
}
