package com.organizaai.infra;

import com.organizaai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class ContaNaoVerificadaScheduler {

    private static final int EXPIRACAO_CADASTRO_MINUTOS = 10;

    private final UserRepository userRepository;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void removerContasNaoVerificadas() {
        Instant cutoff = Instant.now().minus(EXPIRACAO_CADASTRO_MINUTOS, ChronoUnit.MINUTES);
        userRepository.deleteByEmailVerificadoFalseAndCreatedAtBefore(cutoff);
    }
}
