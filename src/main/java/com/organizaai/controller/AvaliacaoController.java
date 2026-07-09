package com.organizaai.controller;

import com.organizaai.data.dto.request.CreateAvaliacaoRequest;
import com.organizaai.data.dto.request.UpdateAvaliacaoRequest;
import com.organizaai.data.dto.response.AvaliacaoResponse;
import com.organizaai.data.entity.Avaliacao;
import com.organizaai.data.entity.User;
import com.organizaai.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @GetMapping
    public List<AvaliacaoResponse> list(
            @AuthenticationPrincipal User user,
            @RequestParam UUID periodoId
    ) {
        return avaliacaoService.list(user.getId(), periodoId);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAvaliacaoRequest request
    ) {
        Avaliacao avaliacao = avaliacaoService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.toResponse(avaliacao));
    }

    @PatchMapping("/{id}")
    public AvaliacaoResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAvaliacaoRequest request
    ) {
        Avaliacao avaliacao = avaliacaoService.update(user.getId(), id, request);
        return avaliacaoService.toResponse(avaliacao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        avaliacaoService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
