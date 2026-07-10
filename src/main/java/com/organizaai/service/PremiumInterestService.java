package com.organizaai.service;

import com.organizaai.data.entity.PremiumInterest;
import com.organizaai.repository.PremiumInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumInterestService {

    private final PremiumInterestRepository premiumInterestRepository;

    public PremiumInterest register(UUID userId) {
        return premiumInterestRepository.findByUserId(userId)
                .orElseGet(() -> premiumInterestRepository.save(new PremiumInterest(userId)));
    }

    public PremiumInterest status(UUID userId) {
        return premiumInterestRepository.findByUserId(userId).orElse(null);
    }
}
