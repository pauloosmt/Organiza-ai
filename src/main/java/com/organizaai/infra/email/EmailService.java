package com.organizaai.infra.email;

import com.organizaai.data.entity.Avaliacao;
import com.organizaai.data.entity.TipoAvaliacao;
import com.organizaai.data.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailService {

    private static final String REMETENTE_NOME = "Organiza Aí";
    private static final DateTimeFormatter FORMATO_DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JavaMailSender mailSender;
    private final String remetente;
    private final String adminNotificationEmail;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String remetente,
            @Value("${app.admin.notification-email:}") String adminNotificationEmail
    ) {
        this.mailSender = mailSender;
        this.remetente = remetente;
        this.adminNotificationEmail = adminNotificationEmail;
    }

    public void enviarCodigoVerificacao(User destinatario, String codigo) {
        String corpo = "<p>Olá, <strong>" + destinatario.getName() + "</strong>!</p>"
                + "<p>Use o código abaixo para confirmar seu email e ativar sua conta:</p>"
                + "<div style=\"text-align:center; margin: 28px 0;\">"
                + "<span style=\"display:inline-block; font-family: 'Courier New', monospace; font-size: 32px; "
                + "font-weight: 700; letter-spacing: 8px; color:#b3131e; background:#fbf9f8; "
                + "border: 1px solid #e6e1de; border-radius: 10px; padding: 14px 20px;\">" + codigo + "</span>"
                + "</div>"
                + "<p>Esse código expira em <strong>15 minutos</strong>.</p>";
        enviar(destinatario.getEmail(), "Confirme seu email - Organiza Aí", montarHtml(corpo, true));
    }

    public void enviarCodigoTrocaSenha(User destinatario, String codigo) {
        String corpo = "<p>Olá, <strong>" + destinatario.getName() + "</strong>!</p>"
                + "<p>Use o código abaixo para confirmar a troca da sua senha:</p>"
                + "<div style=\"text-align:center; margin: 28px 0;\">"
                + "<span style=\"display:inline-block; font-family: 'Courier New', monospace; font-size: 32px; "
                + "font-weight: 700; letter-spacing: 8px; color:#b3131e; background:#fbf9f8; "
                + "border: 1px solid #e6e1de; border-radius: 10px; padding: 14px 20px;\">" + codigo + "</span>"
                + "</div>"
                + "<p>Esse código expira em <strong>15 minutos</strong>.</p>"
                + "<p style=\"margin-top: 24px; font-size: 13px; color:#6b6260;\">Se você não pediu essa troca de "
                + "senha, pode ignorar este email — sua senha continua a mesma.</p>";
        enviar(destinatario.getEmail(), "Confirme a troca de senha - Organiza Aí", montarHtml(corpo, true));
    }

    public void enviarLembreteAvaliacao(User destinatario, Avaliacao avaliacao, String disciplinaNome) {
        String tipo = avaliacao.getTipo() == TipoAvaliacao.PROVA ? "Prova" : "Trabalho";
        String corpo = "<p>Olá, <strong>" + destinatario.getName() + "</strong>!</p>"
                + "<p>Passando pra lembrar que falta <strong>1 semana</strong> para:</p>"
                + "<div style=\"margin: 24px 0; padding: 16px 20px; background:#fbf9f8; "
                + "border-left: 4px solid #b3131e; border-radius: 6px;\">"
                + "<p style=\"margin:0; font-size:13px; text-transform:uppercase; letter-spacing:0.06em; color:#6b6260;\">" + tipo + "</p>"
                + "<p style=\"margin:4px 0 0; font-size:18px; font-weight:700; color:#1a1a1a;\">" + avaliacao.getTitulo() + "</p>"
                + "<p style=\"margin:4px 0 0; color:#6b6260;\">" + disciplinaNome + " &middot; " + avaliacao.getData().format(FORMATO_DATA_BR) + "</p>"
                + "</div>"
                + "<p>Bons estudos!</p>";
        enviar(destinatario.getEmail(), "Lembrete: " + tipo + " de " + disciplinaNome + " em 7 dias", montarHtml(corpo, true));
    }

    public void enviarNotificacaoInteressePremium(User interessado) {
        if (adminNotificationEmail.isBlank()) {
            log.warn("app.admin.notification-email não configurado, pulando notificação de interesse premium.");
            return;
        }
        String corpo = "<p>Novo interesse registrado no plano Premium:</p>"
                + "<div style=\"margin: 24px 0; padding: 16px 20px; background:#fbf9f8; "
                + "border-left: 4px solid #b3131e; border-radius: 6px;\">"
                + "<p style=\"margin:0; font-weight:700; color:#1a1a1a;\">" + interessado.getName() + "</p>"
                + "<p style=\"margin:4px 0 0; color:#6b6260;\">" + interessado.getEmail() + "</p>"
                + "</div>";
        enviar(adminNotificationEmail, "Novo interesse no plano Premium", montarHtml(corpo, false));
    }

    private String montarHtml(String corpoHtml, boolean incluirAvisoSpam) {
        String aviso = !incluirAvisoSpam ? "" :
                "<p style=\"margin-top: 24px; font-size: 13px; color:#6b6260;\">"
                        + "Não encontrou este email? Dá uma olhada na sua caixa de spam/lixo eletrônico.</p>";

        return "<div style=\"font-family: -apple-system, 'Segoe UI', Arial, sans-serif; max-width: 480px; "
                + "margin: 0 auto; border: 1px solid #e6e1de; border-radius: 14px; overflow: hidden;\">"
                + "<div style=\"background:#b3131e; padding: 20px 28px;\">"
                + "<span style=\"font-family: Georgia, serif; font-weight:700; letter-spacing:0.04em; "
                + "color:#ffffff; font-size: 20px;\">ORGANIZA <span style=\"opacity:0.75;\">AÍ</span></span>"
                + "</div>"
                + "<div style=\"padding: 28px; color:#1a1a1a; font-size: 15px; line-height: 1.55;\">"
                + corpoHtml
                + aviso
                + "</div>"
                + "</div>";
    }

    private void enviar(String destinatario, String assunto, String corpoHtml) {
        if (remetente.isBlank()) {
            log.warn("MAIL_USERNAME não configurado, pulando envio de email para {}.", destinatario);
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(remetente, REMETENTE_NOME);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);
            mailSender.send(mimeMessage);
        } catch (Exception ex) {
            log.warn("Falha ao enviar email para {}: {}", destinatario, ex.getMessage());
        }
    }
}
