package com.organizaai.infra.email;

import com.organizaai.data.entity.TipoAvaliacao;
import com.organizaai.data.entity.User;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void configurarMock() {
        lenient().when(mailSender.createMimeMessage()).thenAnswer(
                invocation -> new MimeMessage(Session.getInstance(new Properties()))
        );
    }

    @Test
    void naoEnviaQuandoRemetenteNaoConfigurado() {
        EmailService emailService = new EmailService(mailSender, "", "admin@example.com");
        User destinatario = new User("Usuario", "usuario@example.com", "hash");

        assertDoesNotThrow(() -> emailService.enviarCodigoVerificacao(destinatario, "123456"));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void falhaNoEnvioNaoPropagaParaOChamador() {
        EmailService emailService = new EmailService(mailSender, "remetente@example.com", "admin@example.com");
        User destinatario = new User("Usuario", "usuario@example.com", "hash");
        doThrow(new MailSendException("falha simulada")).when(mailSender).send(any(MimeMessage.class));

        assertDoesNotThrow(() -> emailService.enviarCodigoVerificacao(destinatario, "123456"));
        assertDoesNotThrow(() -> emailService.enviarNotificacaoInteressePremium(destinatario));
        assertDoesNotThrow(() -> emailService.enviarLembreteAvaliacao(
                destinatario,
                new com.organizaai.data.entity.Avaliacao(destinatario.getId(), destinatario.getId(), "Prova 1", TipoAvaliacao.PROVA, LocalDate.now(), 10.0),
                "Disciplina Teste"
        ));
    }
}
