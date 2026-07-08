package com.organizaai.service;

import com.organizaai.data.dto.request.CreateGradeBlocoRequest;
import com.organizaai.data.dto.request.UpdateGradeBlocoRequest;
import com.organizaai.data.dto.response.GradeBlocoResponse;
import com.organizaai.data.entity.Disciplina;
import com.organizaai.data.entity.GradeBloco;
import com.organizaai.exceptions.entity.GradeBlocoNotFoundException;
import com.organizaai.exceptions.entity.HorarioSobrepostoException;
import com.organizaai.repository.DisciplinaRepository;
import com.organizaai.repository.GradeBlocoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {

    private final GradeBlocoRepository gradeBlocoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaService disciplinaService;

    public List<GradeBlocoResponse> list(UUID userId, UUID periodoId) {
        List<Disciplina> disciplinasDoPeriodo = disciplinaRepository.findByUserIdAndPeriodoId(userId, periodoId);
        Map<UUID, Disciplina> disciplinasPorId = disciplinasDoPeriodo.stream()
                .collect(Collectors.toMap(Disciplina::getId, d -> d));

        List<GradeBloco> blocos = gradeBlocoRepository.findByDisciplinaIdIn(disciplinasPorId.keySet());

        return blocos.stream()
                .map(b -> {
                    Disciplina disciplina = disciplinasPorId.get(b.getDisciplinaId());
                    return GradeBlocoResponse.fromEntity(b, disciplina.getNome(), disciplina.getCorIndice());
                })
                .toList();
    }

    public GradeBloco create(UUID userId, CreateGradeBlocoRequest request) {
        Disciplina disciplina = disciplinaService.getOwned(userId, request.disciplinaId());
        validarIntervalo(request.horaInicio(), request.horaFim());
        assertSemSobreposicao(userId, disciplina.getPeriodoId(), request.diaSemana(), request.horaInicio(), request.horaFim(), null);

        GradeBloco bloco = new GradeBloco(userId, disciplina.getId(), request.diaSemana(), request.horaInicio(), request.horaFim());
        return gradeBlocoRepository.save(bloco);
    }

    public GradeBloco update(UUID userId, UUID blocoId, UpdateGradeBlocoRequest request) {
        GradeBloco bloco = getOwned(userId, blocoId);
        Disciplina disciplina = disciplinaService.getOwned(userId, request.disciplinaId());
        validarIntervalo(request.horaInicio(), request.horaFim());
        assertSemSobreposicao(userId, disciplina.getPeriodoId(), request.diaSemana(), request.horaInicio(), request.horaFim(), blocoId);

        bloco.setDisciplinaId(disciplina.getId());
        bloco.setDiaSemana(request.diaSemana());
        bloco.setHoraInicio(request.horaInicio());
        bloco.setHoraFim(request.horaFim());
        return gradeBlocoRepository.save(bloco);
    }

    public void delete(UUID userId, UUID blocoId) {
        gradeBlocoRepository.delete(getOwned(userId, blocoId));
    }

    public GradeBlocoResponse toResponse(GradeBloco bloco) {
        Disciplina disciplina = disciplinaRepository.findById(bloco.getDisciplinaId()).orElse(null);
        String nome = disciplina != null ? disciplina.getNome() : null;
        int corIndice = disciplina != null ? disciplina.getCorIndice() : 0;
        return GradeBlocoResponse.fromEntity(bloco, nome, corIndice);
    }

    private GradeBloco getOwned(UUID userId, UUID blocoId) {
        return gradeBlocoRepository.findByIdAndUserId(blocoId, userId)
                .orElseThrow(() -> new GradeBlocoNotFoundException(blocoId));
    }

    private void validarIntervalo(int horaInicio, int horaFim) {
        if (horaInicio >= horaFim) {
            throw new IllegalArgumentException("horaInicio deve ser menor que horaFim");
        }
    }

    private void assertSemSobreposicao(UUID userId, UUID periodoId, int diaSemana, int horaInicio, int horaFim, UUID ignorarBlocoId) {
        List<UUID> disciplinaIdsDoPeriodo = disciplinaRepository.findByUserIdAndPeriodoId(userId, periodoId).stream()
                .map(Disciplina::getId)
                .toList();

        boolean sobrepoe = gradeBlocoRepository.findByUserIdAndDiaSemana(userId, diaSemana).stream()
                .filter(b -> !b.getId().equals(ignorarBlocoId))
                .filter(b -> disciplinaIdsDoPeriodo.contains(b.getDisciplinaId()))
                .anyMatch(b -> horaInicio < b.getHoraFim() && b.getHoraInicio() < horaFim);

        if (sobrepoe) {
            throw new HorarioSobrepostoException();
        }
    }
}
