package com.organizaai.repository;

import com.organizaai.data.entity.GradeBloco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GradeBlocoRepository extends JpaRepository<GradeBloco, UUID> {

    List<GradeBloco> findByUserId(UUID userId);

    List<GradeBloco> findByUserIdAndDiaSemana(UUID userId, int diaSemana);

    List<GradeBloco> findByDisciplinaIdIn(Collection<UUID> disciplinaIds);

    Optional<GradeBloco> findByIdAndUserId(UUID id, UUID userId);

    long countByDisciplinaId(UUID disciplinaId);

    void deleteByDisciplinaId(UUID disciplinaId);
}
