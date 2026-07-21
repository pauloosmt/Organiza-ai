package com.organizaai.controller;

import com.organizaai.data.dto.request.AtualizarConfigPeriodoRequest;
import com.organizaai.data.dto.request.CreatePeriodoRequest;
import com.organizaai.data.dto.response.PeriodoResponse;
import com.organizaai.data.entity.Periodo;
import com.organizaai.data.entity.User;
import com.organizaai.service.PeriodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/periodos")
@RequiredArgsConstructor
public class PeriodoController {

    private final PeriodoService periodoService;

    @PostMapping
    public ResponseEntity<PeriodoResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreatePeriodoRequest request
    ) {
        Periodo periodo = periodoService.create(user.getId(), request.ano(), request.semestre());
        return ResponseEntity.status(HttpStatus.CREATED).body(PeriodoResponse.fromEntity(periodo));
    }

    @GetMapping
    public List<PeriodoResponse> list(@AuthenticationPrincipal User user) {
        return periodoService.list(user.getId()).stream()
                .map(PeriodoResponse::fromEntity)
                .toList();
    }

    @PutMapping("/{id}/config")
    public ResponseEntity<PeriodoResponse> atualizarConfig(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarConfigPeriodoRequest request
    ) {
        Periodo periodo = periodoService.atualizarConfig(
                user.getId(), id, request.escalaTotal(), request.mediaMinimaPassar(), request.mediaMinimaRecuperacao());
        return ResponseEntity.ok(PeriodoResponse.fromEntity(periodo));
    }
}
