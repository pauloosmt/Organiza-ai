package com.organizaai.service;

import com.organizaai.data.dto.request.CreateAvaliacaoRequest;
import com.organizaai.data.dto.request.UpdateAvaliacaoRequest;
import com.organizaai.data.dto.response.AvaliacaoResponse;
import com.organizaai.data.entity.Avaliacao;
import com.organizaai.data.entity.Disciplina;
import com.organizaai.data.entity.TipoAvaliacao;
import com.organizaai.exceptions.entity.AvaliacaoNotFoundException;
import com.organizaai.exceptions.entity.NotaExcedePontuacaoException;
import com.organizaai.exceptions.entity.NotaSemPontuacaoException;
import com.organizaai.exceptions.entity.PontuacaoObrigatoriaException;
import com.organizaai.repository.AvaliacaoRepository;
import com.organizaai.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaService disciplinaService;

    public List<AvaliacaoResponse> list(UUID userId, UUID periodoId) {
        List<Disciplina> disciplinasDoPeriodo = disciplinaRepository.findByUserIdAndPeriodoId(userId, periodoId);
        Map<UUID, Disciplina> disciplinasPorId = disciplinasDoPeriodo.stream()
                .collect(Collectors.toMap(Disciplina::getId, d -> d));

        List<Avaliacao> avaliacoes = avaliacaoRepository.findByDisciplinaIdIn(disciplinasPorId.keySet());

        return avaliacoes.stream()
                .map(a -> {
                    Disciplina disciplina = disciplinasPorId.get(a.getDisciplinaId());
                    return AvaliacaoResponse.fromEntity(a, disciplina.getNome(), disciplina.getCorIndice());
                })
                .toList();
    }

    public Avaliacao create(UUID userId, CreateAvaliacaoRequest request) {
        validarPontuacaoObrigatoria(request.tipo(), request.pontuacao());
        Disciplina disciplina = disciplinaService.getOwned(userId, request.disciplinaId());
        Avaliacao avaliacao = new Avaliacao(userId, disciplina.getId(), request.titulo(), request.tipo(), request.data(), request.pontuacao());
        return avaliacaoRepository.save(avaliacao);
    }

    public Avaliacao update(UUID userId, UUID avaliacaoId, UpdateAvaliacaoRequest request) {
        validarPontuacaoObrigatoria(request.tipo(), request.pontuacao());
        if (request.nota() != null) {
            if (request.pontuacao() == null) {
                throw new NotaSemPontuacaoException();
            }
            if (request.nota() > request.pontuacao()) {
                throw new NotaExcedePontuacaoException(request.pontuacao());
            }
        }
        Avaliacao avaliacao = getOwned(userId, avaliacaoId);
        avaliacao.setTitulo(request.titulo());
        avaliacao.setTipo(request.tipo());
        avaliacao.setData(request.data());
        avaliacao.setPontuacao(request.pontuacao());
        avaliacao.setNota(request.nota());
        return avaliacaoRepository.save(avaliacao);
    }

    private void validarPontuacaoObrigatoria(TipoAvaliacao tipo, Double pontuacao) {
        if (tipo != TipoAvaliacao.ATIVIDADE && pontuacao == null) {
            throw new PontuacaoObrigatoriaException();
        }
    }

    public void delete(UUID userId, UUID avaliacaoId) {
        avaliacaoRepository.delete(getOwned(userId, avaliacaoId));
    }

    public AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        Disciplina disciplina = disciplinaRepository.findById(avaliacao.getDisciplinaId()).orElse(null);
        String nome = disciplina != null ? disciplina.getNome() : null;
        int corIndice = disciplina != null ? disciplina.getCorIndice() : 0;
        return AvaliacaoResponse.fromEntity(avaliacao, nome, corIndice);
    }

    private Avaliacao getOwned(UUID userId, UUID avaliacaoId) {
        return avaliacaoRepository.findByIdAndUserId(avaliacaoId, userId)
                .orElseThrow(() -> new AvaliacaoNotFoundException(avaliacaoId));
    }
}
