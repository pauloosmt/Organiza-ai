package com.organizaai.controller;

import com.organizaai.data.dto.request.EsqueciSenhaRequest;
import com.organizaai.data.dto.request.LoginRequest;
import com.organizaai.data.dto.request.RedefinirSenhaRequest;
import com.organizaai.data.dto.request.ReenviarCodigoRequest;
import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.VerificarEmailRequest;
import com.organizaai.data.dto.response.UserResponse;
import com.organizaai.data.entity.User;
import com.organizaai.infra.security.JwtService;
import com.organizaai.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromEntity(user));
    }

    @PostMapping("/verificar-email")
    public ResponseEntity<Void> verificarEmail(@Valid @RequestBody VerificarEmailRequest request) {
        userService.verificarEmail(request.email(), request.codigo());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reenviar-codigo")
    public ResponseEntity<Void> reenviarCodigo(@Valid @RequestBody ReenviarCodigoRequest request) {
        userService.reenviarCodigo(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequest request) {
        userService.solicitarRecuperacaoSenha(request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequest request) {
        userService.redefinirSenha(request.email(), request.codigo(), request.novaSenha());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticate(request.email(), request.password());
        String token = jwtService.generateToken(user.getEmail());
        ResponseCookie cookie = jwtService.buildCookie(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(UserResponse.fromEntity(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = jwtService.buildExpiredCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }
}
