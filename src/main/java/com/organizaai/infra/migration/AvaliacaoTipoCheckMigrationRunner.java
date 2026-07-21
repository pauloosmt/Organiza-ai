package com.organizaai.infra.migration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * O Hibernate gera uma CHECK CONSTRAINT em cima da lista de valores do enum
 * {@code TipoAvaliacao} no momento em que a tabela é criada, mas
 * ddl-auto=update não atualiza essa constraint quando o enum ganha um valor
 * novo (caso do spec 013, que adicionou RECUPERACAO) — bancos já existentes
 * ficam com a constraint antiga, rejeitando o valor novo. Remove a
 * constraint (idempotente); sem ela, a validação do enum continua garantida
 * a nível de aplicação pelo próprio Java.
 */
@Component
public class AvaliacaoTipoCheckMigrationRunner implements ApplicationRunner {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        entityManager.createNativeQuery(
                "ALTER TABLE avaliacoes DROP CONSTRAINT IF EXISTS avaliacoes_tipo_check"
        ).executeUpdate();
    }
}
