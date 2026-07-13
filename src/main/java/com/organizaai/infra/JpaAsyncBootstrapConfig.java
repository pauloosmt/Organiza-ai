package com.organizaai.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Faz o Hibernate montar o EntityManagerFactory em background, sem travar
 * a subida do resto da aplicação (incluindo o Tomcat/health check). Sem
 * isso, se o Postgres estiver inalcançável no boot (banco gratuito da
 * Render hiberna), o app inteiro fica travado esperando — mesmo com
 * lazy-init e bootstrap-mode=deferred, que sozinhos não bastam.
 *
 * Esse bean é reaproveitado pelo Spring Boot pra outros usos internos de
 * "applicationTaskExecutor", não só pro bootstrap do JPA — por isso precisa
 * de mais de uma thread, senão uma conexão de banco travada monopoliza o
 * único worker disponível e trava tudo que depender dele.
 */
@Configuration
public class JpaAsyncBootstrapConfig {

    @Bean(name = "applicationTaskExecutor")
    public AsyncTaskExecutor applicationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("app-task-");
        executor.initialize();
        return executor;
    }
}
