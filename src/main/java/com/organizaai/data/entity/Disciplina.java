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

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private int faltas;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Disciplina(UUID userId, String nome) {
        this.userId = userId;
        this.nome = nome;
        this.faltas = 0;
        this.createdAt = Instant.now();
    }
}
