package com.organizaai.repository;

import com.organizaai.data.entity.PremiumInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PremiumInterestRepository extends JpaRepository<PremiumInterest, UUID> {

    Optional<PremiumInterest> findByUserId(UUID userId);

    long count();
}
