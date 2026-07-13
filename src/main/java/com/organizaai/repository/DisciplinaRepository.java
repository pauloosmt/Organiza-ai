package com.organizaai.repository;

import com.organizaai.data.entity.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisciplinaRepository extends JpaRepository<Disciplina, UUID> {

    List<Disciplina> findByUserId(UUID userId);

    List<Disciplina> findByUserIdAndPeriodoId(UUID userId, UUID periodoId);

    List<Disciplina> findByPeriodoIdIsNull();

    Optional<Disciplina> findByIdAndUserId(UUID id, UUID userId);

    Optional<Disciplina> findByIdAndUserIdAndPeriodoId(UUID id, UUID userId, UUID periodoId);

    long countByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
