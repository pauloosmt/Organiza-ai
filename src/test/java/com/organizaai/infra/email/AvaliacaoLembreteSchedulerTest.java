package com.organizaai.infra.email;

import com.organizaai.data.entity.Avaliacao;
import com.organizaai.data.entity.Disciplina;
import com.organizaai.data.entity.TipoAvaliacao;
import com.organizaai.data.entity.User;
import com.organizaai.repository.AvaliacaoRepository;
import com.organizaai.repository.DisciplinaRepository;
import com.organizaai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliacaoLembreteSchedulerTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private DisciplinaRepository disciplinaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AvaliacaoLembreteScheduler scheduler;

    @Test
    void buscaAvaliacoesDeExatosSeteDiasAFrenteEEnviaLembrete() {
        UUID userId = UUID.randomUUID();
        UUID disciplinaId = UUID.randomUUID();
        LocalDate dataAlvo = LocalDate.now().plusDays(7);

        Avaliacao avaliacao = new Avaliacao(userId, disciplinaId, "Prova 1", TipoAvaliacao.PROVA, dataAlvo, 10.0);
        Disciplina disciplina = new Disciplina(userId, UUID.randomUUID(), "Cálculo I", 0);
        User dono = new User("Usuario Teste", "usuario@example.com", "hash");

        when(avaliacaoRepository.findByDataAndLembreteEnviadoFalse(eq(dataAlvo))).thenReturn(List.of(avaliacao));
        when(disciplinaRepository.findById(disciplinaId)).thenReturn(Optional.of(disciplina));
        when(userRepository.findById(userId)).thenReturn(Optional.of(dono));

        scheduler.enviarLembretes();

        verify(emailService).enviarLembreteAvaliacao(eq(dono), eq(avaliacao), eq("Cálculo I"));

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(avaliacaoRepository).save(captor.capture());
        assertTrue(captor.getValue().isLembreteEnviado());
    }

    @Test
    void naoEnviaLembreteSeDisciplinaNaoExisteMais() {
        UUID userId = UUID.randomUUID();
        UUID disciplinaId = UUID.randomUUID();
        LocalDate dataAlvo = LocalDate.now().plusDays(7);
        Avaliacao avaliacao = new Avaliacao(userId, disciplinaId, "Prova 1", TipoAvaliacao.PROVA, dataAlvo, 10.0);

        when(avaliacaoRepository.findByDataAndLembreteEnviadoFalse(eq(dataAlvo))).thenReturn(List.of(avaliacao));
        when(disciplinaRepository.findById(disciplinaId)).thenReturn(Optional.empty());

        scheduler.enviarLembretes();

        verify(emailService, org.mockito.Mockito.never()).enviarLembreteAvaliacao(any(), any(), any());
        verify(avaliacaoRepository, org.mockito.Mockito.never()).save(any());
    }
}
