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
@Table(name = "periodos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Periodo {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 7)
    private String nome;

    /**
     * Defaults a nível de banco (via ColumnDefault) pra retrocompatibilidade
     * com ddl-auto=update, mesmo truque usado em User.emailVerificado e
     * Disciplina.corIndice: períodos já existentes herdam 10/6/3 do banco;
     * períodos novos recebem o valor explícito do construtor.
     */
    @Column(name = "escala_total", nullable = false)
    @ColumnDefault("10")
    private double escalaTotal;

    @Column(name = "media_minima_passar", nullable = false)
    @ColumnDefault("6")
    private double mediaMinimaPassar;

    @Column(name = "media_minima_recuperacao", nullable = false)
    @ColumnDefault("3")
    private double mediaMinimaRecuperacao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Periodo(UUID userId, String nome) {
        this.userId = userId;
        this.nome = nome;
        this.escalaTotal = 10;
        this.mediaMinimaPassar = 6;
        this.mediaMinimaRecuperacao = 3;
        this.createdAt = Instant.now();
    }
}
