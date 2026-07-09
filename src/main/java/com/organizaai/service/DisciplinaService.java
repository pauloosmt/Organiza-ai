package com.organizaai.service;

import com.organizaai.data.dto.response.DisciplinaResponse;
import com.organizaai.data.entity.Disciplina;
import com.organizaai.data.entity.GradeBloco;
import com.organizaai.exceptions.entity.DisciplinaNotFoundException;
import com.organizaai.exceptions.entity.FaltasNegativasException;
import com.organizaai.repository.AvaliacaoRepository;
import com.organizaai.repository.DisciplinaRepository;
import com.organizaai.repository.GradeBlocoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private static final int QUANTIDADE_CORES = 5;

    private final DisciplinaRepository disciplinaRepository;
    private final GradeBlocoRepository gradeBlocoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public Disciplina create(UUID userId, UUID periodoId, String nome) {
        int corIndice = (int) (disciplinaRepository.countByUserId(userId) % QUANTIDADE_CORES);
        return disciplinaRepository.save(new Disciplina(userId, periodoId, nome, corIndice));
    }

    public List<DisciplinaResponse> list(UUID userId, UUID periodoId) {
        List<Disciplina> disciplinas = disciplinaRepository.findByUserIdAndPeriodoId(userId, periodoId);
        Map<UUID, Long> quantidadeBlocosPorDisciplina = gradeBlocoRepository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(GradeBloco::getDisciplinaId, Collectors.counting()));

        return disciplinas.stream()
                .map(d -> DisciplinaResponse.fromEntity(d, quantidadeBlocosPorDisciplina.getOrDefault(d.getId(), 0L)))
                .toList();
    }

    public long countBlocos(UUID disciplinaId) {
        return gradeBlocoRepository.countByDisciplinaId(disciplinaId);
    }

    public Disciplina getOwned(UUID userId, UUID disciplinaId) {
        return disciplinaRepository.findByIdAndUserId(disciplinaId, userId)
                .orElseThrow(() -> new DisciplinaNotFoundException(disciplinaId));
    }

    @Transactional
    public void delete(UUID userId, UUID disciplinaId) {
        Disciplina disciplina = getOwned(userId, disciplinaId);
        gradeBlocoRepository.deleteByDisciplinaId(disciplina.getId());
        avaliacaoRepository.deleteByDisciplinaId(disciplina.getId());
        disciplinaRepository.delete(disciplina);
    }

    public Disciplina ajustarFaltas(UUID userId, UUID disciplinaId, int delta) {
        if (delta != 1 && delta != -1) {
            throw new IllegalArgumentException("delta deve ser 1 ou -1");
        }

        Disciplina disciplina = getOwned(userId, disciplinaId);
        int novasFaltas = disciplina.getFaltas() + delta;
        if (novasFaltas < 0) {
            throw new FaltasNegativasException();
        }

        disciplina.setFaltas(novasFaltas);
        return disciplinaRepository.save(disciplina);
    }
}
