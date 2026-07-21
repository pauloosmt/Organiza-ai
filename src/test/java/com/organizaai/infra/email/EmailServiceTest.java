package com.organizaai.infra.email;

import com.organizaai.data.entity.TipoAvaliacao;
import com.organizaai.data.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private RestClient restClient;

    private EmailService criarEmailService(String remetente, String clientId, String clientSecret, String refreshToken, String adminEmail) {
        restClient = Mockito.mock(RestClient.class, Mockito.RETURNS_DEEP_STUBS);
        return new EmailService(restClient, remetente, clientId, clientSecret, refreshToken, adminEmail);
    }

    @Test
    void naoEnviaQuandoRemetenteNaoConfigurado() {
        EmailService emailService = criarEmailService("", "client-id", "client-secret", "refresh-token", "admin@example.com");
        User destinatario = new User("Usuario", "usuario@example.com", "hash");

        assertDoesNotThrow(() -> emailService.enviarCodigoVerificacao(destinatario, "123456"));

        verify(restClient, never()).post();
    }

    @Test
    void naoEnviaQuandoCredenciaisGmailNaoConfiguradas() {
        EmailService emailService = criarEmailService("remetente@example.com", "", "", "", "admin@example.com");
        User destinatario = new User("Usuario", "usuario@example.com", "hash");

        assertDoesNotThrow(() -> emailService.enviarCodigoVerificacao(destinatario, "123456"));

        verify(restClient, never()).post();
    }

    @Test
    void falhaNoEnvioNaoPropagaParaOChamador() {
        EmailService emailService = criarEmailService(
                "remetente@example.com", "client-id", "client-secret", "refresh-token", "admin@example.com");
        User destinatario = new User("Usuario", "usuario@example.com", "hash");
        when(restClient.post()).thenThrow(new RuntimeException("falha simulada"));

        assertDoesNotThrow(() -> emailService.enviarCodigoVerificacao(destinatario, "123456"));
        assertDoesNotThrow(() -> emailService.enviarCodigoRecuperacaoSenha(destinatario, "123456"));
        assertDoesNotThrow(() -> emailService.enviarNotificacaoInteressePremium(destinatario));
        assertDoesNotThrow(() -> emailService.enviarLembreteAvaliacao(
                destinatario,
                new com.organizaai.data.entity.Avaliacao(destinatario.getId(), destinatario.getId(), "Prova 1", TipoAvaliacao.PROVA, LocalDate.now(), 10.0),
                "Disciplina Teste"
        ));
    }
}
