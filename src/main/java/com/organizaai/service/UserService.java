package com.organizaai.service;

import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.UpdateUserRequest;
import com.organizaai.data.entity.User;
import com.organizaai.exceptions.entity.CodigoVerificacaoInvalidoException;
import com.organizaai.exceptions.entity.EmailAlreadyExistsException;
import com.organizaai.exceptions.entity.SenhaAtualInvalidaException;
import com.organizaai.exceptions.login.EmailNaoVerificadoException;
import com.organizaai.exceptions.login.InvalidCredentialsException;
import com.organizaai.infra.email.EmailService;
import com.organizaai.repository.AvaliacaoRepository;
import com.organizaai.repository.DisciplinaRepository;
import com.organizaai.repository.GradeBlocoRepository;
import com.organizaai.repository.PeriodoRepository;
import com.organizaai.repository.PremiumInterestRepository;
import com.organizaai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int CODIGO_EXPIRACAO_MINUTOS = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PeriodoRepository periodoRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final GradeBlocoRepository gradeBlocoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PremiumInterestRepository premiumInterestRepository;

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = new User(request.name(), request.email(), passwordEncoder.encode(request.password()));
        gerarEEnviarCodigo(user);
        return userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEmailVerificado()) {
            throw new EmailNaoVerificadoException();
        }
        return user;
    }

    public void verificarEmail(String email, String codigo) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(CodigoVerificacaoInvalidoException::new);
        if (user.isEmailVerificado()) {
            return;
        }
        boolean codigoValido = codigo != null && codigo.equals(user.getCodigoVerificacao());
        boolean naoExpirado = user.getCodigoVerificacaoExpiraEm() != null
                && Instant.now().isBefore(user.getCodigoVerificacaoExpiraEm());
        if (!codigoValido || !naoExpirado) {
            throw new CodigoVerificacaoInvalidoException();
        }
        user.setEmailVerificado(true);
        user.setCodigoVerificacao(null);
        user.setCodigoVerificacaoExpiraEm(null);
        userRepository.save(user);
    }

    public void reenviarCodigo(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            if (user.isEmailVerificado()) {
                return;
            }
            gerarEEnviarCodigo(user);
            userRepository.save(user);
        });
    }

    private void gerarEEnviarCodigo(User user) {
        String codigo = gerarCodigo();
        user.setCodigoVerificacao(codigo);
        user.setCodigoVerificacaoExpiraEm(Instant.now().plus(CODIGO_EXPIRACAO_MINUTOS, ChronoUnit.MINUTES));
        emailService.enviarCodigoVerificacao(user, codigo);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    public User updateProfile(User user, UpdateUserRequest request) {
        user.setName(request.name());
        return userRepository.save(user);
    }

    public void iniciarTrocaSenha(User user, String novaSenha) {
        String codigo = gerarCodigo();
        user.setNovaSenhaHash(passwordEncoder.encode(novaSenha));
        user.setCodigoTrocaSenha(codigo);
        user.setCodigoTrocaSenhaExpiraEm(Instant.now().plus(CODIGO_EXPIRACAO_MINUTOS, ChronoUnit.MINUTES));
        userRepository.save(user);
        emailService.enviarCodigoTrocaSenha(user, codigo);
    }

    public void confirmarTrocaSenha(User user, String codigo) {
        boolean codigoValido = codigo != null && codigo.equals(user.getCodigoTrocaSenha());
        boolean naoExpirado = user.getCodigoTrocaSenhaExpiraEm() != null
                && Instant.now().isBefore(user.getCodigoTrocaSenhaExpiraEm());
        if (user.getNovaSenhaHash() == null || !codigoValido || !naoExpirado) {
            throw new CodigoVerificacaoInvalidoException();
        }
        user.setPasswordHash(user.getNovaSenhaHash());
        limparTrocaSenhaPendente(user);
        userRepository.save(user);
    }

    public void reenviarCodigoTrocaSenha(User user) {
        if (user.getNovaSenhaHash() == null) {
            return;
        }
        String codigo = gerarCodigo();
        user.setCodigoTrocaSenha(codigo);
        user.setCodigoTrocaSenhaExpiraEm(Instant.now().plus(CODIGO_EXPIRACAO_MINUTOS, ChronoUnit.MINUTES));
        userRepository.save(user);
        emailService.enviarCodigoTrocaSenha(user, codigo);
    }

    public User atualizarTema(User user, String tema) {
        user.setTema(tema);
        return userRepository.save(user);
    }

    @Transactional
    public void excluirConta(User user, String senhaAtual) {
        if (!passwordEncoder.matches(senhaAtual, user.getPasswordHash())) {
            throw new SenhaAtualInvalidaException();
        }
        gradeBlocoRepository.deleteByUserId(user.getId());
        avaliacaoRepository.deleteByUserId(user.getId());
        disciplinaRepository.deleteByUserId(user.getId());
        periodoRepository.deleteByUserId(user.getId());
        premiumInterestRepository.findByUserId(user.getId()).ifPresent(premiumInterestRepository::delete);
        userRepository.delete(user);
    }

    private void limparTrocaSenhaPendente(User user) {
        user.setNovaSenhaHash(null);
        user.setCodigoTrocaSenha(null);
        user.setCodigoTrocaSenhaExpiraEm(null);
    }

    private String gerarCodigo() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }
}
