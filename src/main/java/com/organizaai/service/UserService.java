package com.organizaai.service;

import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.UpdateUserRequest;
import com.organizaai.data.entity.User;
import com.organizaai.exceptions.entity.CodigoVerificacaoInvalidoException;
import com.organizaai.exceptions.entity.EmailAlreadyExistsException;
import com.organizaai.exceptions.login.EmailNaoVerificadoException;
import com.organizaai.exceptions.login.InvalidCredentialsException;
import com.organizaai.infra.email.EmailService;
import com.organizaai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        String codigo = String.format("%06d", RANDOM.nextInt(1_000_000));
        user.setCodigoVerificacao(codigo);
        user.setCodigoVerificacaoExpiraEm(Instant.now().plus(CODIGO_EXPIRACAO_MINUTOS, ChronoUnit.MINUTES));
        emailService.enviarCodigoVerificacao(user, codigo);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
    }

    public User updateProfile(User user, UpdateUserRequest request) {
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        return userRepository.save(user);
    }
}
