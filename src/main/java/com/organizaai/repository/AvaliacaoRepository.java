package com.organizaai.repository;

import com.organizaai.data.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    List<Avaliacao> findByDisciplinaIdIn(Collection<UUID> disciplinaIds);

    Optional<Avaliacao> findByIdAndUserId(UUID id, UUID userId);

    void deleteByDisciplinaId(UUID disciplinaId);
}
