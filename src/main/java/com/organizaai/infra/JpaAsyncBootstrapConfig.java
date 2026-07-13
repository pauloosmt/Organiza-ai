package com.organizaai.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

/**
 * Faz o Hibernate montar o EntityManagerFactory em background, sem travar
 * a subida do resto da aplicação (incluindo o Tomcat/health check). Sem
 * isso, se o Postgres estiver inalcançável no boot (banco gratuito da
 * Render hiberna), o app inteiro fica travado esperando — mesmo com
 * lazy-init e bootstrap-mode=deferred, que sozinhos não bastam.
 */
@Configuration
public class JpaAsyncBootstrapConfig {

    @Bean(name = "applicationTaskExecutor")
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newSingleThreadExecutor());
    }
}
