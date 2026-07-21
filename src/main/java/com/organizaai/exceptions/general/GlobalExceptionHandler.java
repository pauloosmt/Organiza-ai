package com.organizaai.exceptions.general;

import com.organizaai.exceptions.entity.AvaliacaoNotFoundException;
import com.organizaai.exceptions.entity.CodigoVerificacaoInvalidoException;
import com.organizaai.exceptions.entity.ConfigPeriodoInvalidaException;
import com.organizaai.exceptions.entity.DisciplinaNotFoundException;
import com.organizaai.exceptions.entity.EmailAlreadyExistsException;
import com.organizaai.exceptions.entity.FaltasNegativasException;
import com.organizaai.exceptions.entity.GradeBlocoNotFoundException;
import com.organizaai.exceptions.entity.HorarioSobrepostoException;
import com.organizaai.exceptions.entity.NotaExcedePontuacaoException;
import com.organizaai.exceptions.entity.PeriodoJaExisteException;
import com.organizaai.exceptions.entity.PeriodoNotFoundException;
import com.organizaai.exceptions.entity.SenhaAtualInvalidaException;
import com.organizaai.exceptions.login.EmailNaoVerificadoException;
import com.organizaai.exceptions.login.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(EmailNaoVerificadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNaoVerificado(EmailNaoVerificadoException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(CodigoVerificacaoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoVerificacaoInvalido(CodigoVerificacaoInvalidoException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SenhaAtualInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleSenhaAtualInvalida(SenhaAtualInvalidaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConfigPeriodoInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleConfigPeriodoInvalida(ConfigPeriodoInvalidaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NotaExcedePontuacaoException.class)
    public ResponseEntity<Map<String, Object>> handleNotaExcedePontuacao(NotaExcedePontuacaoException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({DisciplinaNotFoundException.class, GradeBlocoNotFoundException.class, AvaliacaoNotFoundException.class, PeriodoNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({HorarioSobrepostoException.class, PeriodoJaExisteException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(RuntimeException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(FaltasNegativasException.class)
    public ResponseEntity<Map<String, Object>> handleFaltasNegativas(FaltasNegativasException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Dados inválidos");
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
