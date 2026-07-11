package com.organizaai.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "disciplina_id", nullable = false)
    private UUID disciplinaId;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private TipoAvaliacao tipo;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private double pontuacao;

    @Column
    private Double nota;

    @Column(name = "lembrete_enviado", nullable = false)
    @ColumnDefault("false")
    private boolean lembreteEnviado;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Avaliacao(UUID userId, UUID disciplinaId, String titulo, TipoAvaliacao tipo, LocalDate data, double pontuacao) {
        this.userId = userId;
        this.disciplinaId = disciplinaId;
        this.titulo = titulo;
        this.tipo = tipo;
        this.data = data;
        this.pontuacao = pontuacao;
        this.nota = null;
        this.lembreteEnviado = false;
        this.createdAt = Instant.now();
    }
}
