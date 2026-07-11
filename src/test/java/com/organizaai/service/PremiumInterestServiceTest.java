package com.organizaai.service;

import com.organizaai.data.entity.PremiumInterest;
import com.organizaai.data.entity.User;
import com.organizaai.infra.email.EmailService;
import com.organizaai.repository.PremiumInterestRepository;
import com.organizaai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumInterestServiceTest {

    @Mock
    private PremiumInterestRepository premiumInterestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PremiumInterestService premiumInterestService;

    @Test
    void registrarInteresseNotificaODonoDoSistemaPorEmail() {
        UUID userId = UUID.randomUUID();
        User user = new User("Usuario Teste", "teste@example.com", "hash");

        when(premiumInterestRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(premiumInterestRepository.save(any(PremiumInterest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        PremiumInterest resultado = premiumInterestService.register(userId);

        assertNotNull(resultado);
        verify(premiumInterestRepository).save(any(PremiumInterest.class));
        verify(emailService).enviarNotificacaoInteressePremium(user);
    }

    @Test
    void registrarInteresseDuasVezesNaoNotificaDeNovo() {
        UUID userId = UUID.randomUUID();
        PremiumInterest existente = new PremiumInterest(userId);
        when(premiumInterestRepository.findByUserId(userId)).thenReturn(Optional.of(existente));

        PremiumInterest resultado = premiumInterestService.register(userId);

        assertNotNull(resultado);
        verify(premiumInterestRepository, org.mockito.Mockito.never()).save(any());
        verify(emailService, org.mockito.Mockito.never()).enviarNotificacaoInteressePremium(any());
    }
}
