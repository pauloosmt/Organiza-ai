package com.organizaai.controller;

import com.organizaai.data.dto.request.CreateGradeBlocoRequest;
import com.organizaai.data.dto.request.UpdateGradeBlocoRequest;
import com.organizaai.data.dto.response.GradeBlocoResponse;
import com.organizaai.data.entity.GradeBloco;
import com.organizaai.data.entity.User;
import com.organizaai.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/grade/blocos")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    public List<GradeBlocoResponse> list(@AuthenticationPrincipal User user) {
        return gradeService.list(user.getId());
    }

    @PostMapping
    public ResponseEntity<GradeBlocoResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateGradeBlocoRequest request
    ) {
        GradeBloco bloco = gradeService.create(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeService.toResponse(bloco));
    }

    @PutMapping("/{id}")
    public GradeBlocoResponse update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGradeBlocoRequest request
    ) {
        GradeBloco bloco = gradeService.update(user.getId(), id, request);
        return gradeService.toResponse(bloco);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        gradeService.delete(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
