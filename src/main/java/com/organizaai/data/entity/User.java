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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Default TRUE a nível de banco de propósito: o {@code ddl-auto=update}
     * aplica esse default retroativamente em todos os usuários já
     * existentes (grandfathered, sem precisar de migration runner). Todo
     * cadastro novo passa por este construtor, que seta explicitamente
     * {@code false} — o INSERT do Hibernate sempre especifica o valor,
     * ignorando o default do banco. Mesmo truque já usado com
     * {@code corIndice} em {@code Disciplina}.
     */
    @Column(name = "email_verificado", nullable = false)
    @ColumnDefault("true")
    private boolean emailVerificado;

    @Column(name = "codigo_verificacao")
    private String codigoVerificacao;

    @Column(name = "codigo_verificacao_expira_em")
    private Instant codigoVerificacaoExpiraEm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.emailVerificado = false;
        this.createdAt = Instant.now();
    }
}
