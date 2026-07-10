package com.organizaai.controller;

import com.organizaai.data.dto.response.PremiumInterestResponse;
import com.organizaai.data.entity.PremiumInterest;
import com.organizaai.data.entity.User;
import com.organizaai.service.PremiumInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/premium/interesse")
@RequiredArgsConstructor
public class PremiumInterestController {

    private final PremiumInterestService premiumInterestService;

    @PostMapping
    public PremiumInterestResponse register(@AuthenticationPrincipal User user) {
        PremiumInterest interest = premiumInterestService.register(user.getId());
        return PremiumInterestResponse.registrado(interest);
    }

    @GetMapping
    public PremiumInterestResponse status(@AuthenticationPrincipal User user) {
        PremiumInterest interest = premiumInterestService.status(user.getId());
        return interest == null ? PremiumInterestResponse.naoRegistrado() : PremiumInterestResponse.registrado(interest);
    }
}
