package com.organizaai.data.dto.response;

import com.organizaai.data.entity.PremiumInterest;

import java.time.Instant;

public record PremiumInterestResponse(
        boolean registrado,
        Instant createdAt
) {
    public static PremiumInterestResponse registrado(PremiumInterest interest) {
        return new PremiumInterestResponse(true, interest.getCreatedAt());
    }

    public static PremiumInterestResponse naoRegistrado() {
        return new PremiumInterestResponse(false, null);
    }
}
