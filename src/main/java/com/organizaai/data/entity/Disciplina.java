package com.organizaai.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "disciplinas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Disciplina {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Nullable a nível de banco de propósito: o {@code ddl-auto=update} não
     * consegue adicionar uma coluna NOT NULL numa tabela já populada. O
     * {@code PeriodoMigrationRunner} garante que, na prática, nunca fica
     * nula; toda criação nova já exige o valor via DTO.
     */
    @Column(name = "periodo_id")
    private UUID periodoId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int faltas;

    @Column(name = "cor_indice", nullable = false)
    @ColumnDefault("0")
    private int corIndice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Disciplina(UUID userId, UUID periodoId, String nome, int corIndice) {
        this.userId = userId;
        this.periodoId = periodoId;
        this.nome = nome;
        this.faltas = 0;
        this.corIndice = corIndice;
        this.createdAt = Instant.now();
    }
}
