package com.organizaai.controller;

import com.organizaai.data.dto.request.AtualizarTemaRequest;
import com.organizaai.data.dto.request.ConfirmarTrocaSenhaRequest;
import com.organizaai.data.dto.request.ExcluirContaRequest;
import com.organizaai.data.dto.request.TrocarSenhaRequest;
import com.organizaai.data.dto.request.UpdateUserRequest;
import com.organizaai.data.dto.response.UserResponse;
import com.organizaai.data.entity.User;
import com.organizaai.infra.security.JwtService;
import com.organizaai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return UserResponse.fromEntity(user);
    }

    @PutMapping("/me")
    public UserResponse updateMe(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateUserRequest request) {
        User updated = userService.updateProfile(user, request);
        return UserResponse.fromEntity(updated);
    }

    @PutMapping("/me/tema")
    public UserResponse atualizarTema(@AuthenticationPrincipal User user, @Valid @RequestBody AtualizarTemaRequest request) {
        User updated = userService.atualizarTema(user, request.tema());
        return UserResponse.fromEntity(updated);
    }

    @PostMapping("/me/senha")
    public ResponseEntity<Void> iniciarTrocaSenha(@AuthenticationPrincipal User user, @Valid @RequestBody TrocarSenhaRequest request) {
        userService.iniciarTrocaSenha(user, request.novaSenha());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/senha/confirmar")
    public ResponseEntity<Void> confirmarTrocaSenha(@AuthenticationPrincipal User user, @Valid @RequestBody ConfirmarTrocaSenhaRequest request) {
        userService.confirmarTrocaSenha(user, request.codigo());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/senha/reenviar")
    public ResponseEntity<Void> reenviarCodigoTrocaSenha(@AuthenticationPrincipal User user) {
        userService.reenviarCodigoTrocaSenha(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> excluirConta(@AuthenticationPrincipal User user, @Valid @RequestBody ExcluirContaRequest request) {
        userService.excluirConta(user, request.senha());
        ResponseCookie cookie = jwtService.buildExpiredCookie();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
