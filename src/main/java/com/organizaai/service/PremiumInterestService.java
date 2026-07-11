package com.organizaai.service;

import com.organizaai.data.entity.PremiumInterest;
import com.organizaai.infra.email.EmailService;
import com.organizaai.repository.PremiumInterestRepository;
import com.organizaai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumInterestService {

    private final PremiumInterestRepository premiumInterestRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public PremiumInterest register(UUID userId) {
        return premiumInterestRepository.findByUserId(userId)
                .orElseGet(() -> {
                    PremiumInterest interest = premiumInterestRepository.save(new PremiumInterest(userId));
                    userRepository.findById(userId).ifPresent(emailService::enviarNotificacaoInteressePremium);
                    return interest;
                });
    }

    public PremiumInterest status(UUID userId) {
        return premiumInterestRepository.findByUserId(userId).orElse(null);
    }
}
