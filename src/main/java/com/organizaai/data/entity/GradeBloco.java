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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "grade_blocos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeBloco {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "disciplina_id", nullable = false)
    private UUID disciplinaId;

    @Column(name = "dia_semana", nullable = false)
    private int diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private int horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private int horaFim;

    @Column(name = "sala", length = 80)
    private String sala;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public GradeBloco(UUID userId, UUID disciplinaId, int diaSemana, int horaInicio, int horaFim) {
        this.userId = userId;
        this.disciplinaId = disciplinaId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.createdAt = Instant.now();
    }
}
