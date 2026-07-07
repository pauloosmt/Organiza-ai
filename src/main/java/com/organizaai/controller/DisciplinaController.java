package com.organizaai.controller;

import com.organizaai.data.dto.request.AjustarFaltasRequest;
import com.organizaai.data.dto.request.CreateDisciplinaRequest;
import com.organizaai.data.dto.response.DisciplinaResponse;
import com.organizaai.data.entity.Disciplina;
import com.organizaai.data.entity.User;
import com.organizaai.service.DisciplinaService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    @PostMapping
    public ResponseEntity<DisciplinaResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateDisciplinaRequest request
    ) {
        Disciplina disciplina = disciplinaService.create(user.getId(), request.nome());
        return ResponseEntity.status(HttpStatus.CREATED).body(DisciplinaResponse.fromEntity(disciplina, 0));
    }

    @GetMapping
    public List<DisciplinaResponse> list(@AuthenticationPrincipal User user) {
        return disciplinaService.list(user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        disciplinaService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/faltas")
    public DisciplinaResponse ajustarFaltas(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody AjustarFaltasRequest request
    ) {
        Disciplina disciplina = disciplinaService.ajustarFaltas(user.getId(), id, request.delta());
        return DisciplinaResponse.fromEntity(disciplina, disciplinaService.countBlocos(disciplina.getId()));
    }
}
