package com.organizaai.repository;

import com.organizaai.data.entity.Periodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PeriodoRepository extends JpaRepository<Periodo, UUID> {

    List<Periodo> findByUserIdOrderByNomeDesc(UUID userId);

    Optional<Periodo> findByUserIdAndNome(UUID userId, String nome);

    Optional<Periodo> findByIdAndUserId(UUID id, UUID userId);

    void deleteByUserId(UUID userId);
}
