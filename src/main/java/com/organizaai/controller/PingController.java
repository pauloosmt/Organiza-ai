package com.organizaai.controller;

import com.organizaai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint separado do /actuator/health: aquele decide se um deploy novo no
 * Render esta saudavel e nao pode depender do Postgres (que tambem pode estar
 * hibernando e nao acordar a tempo, travando o deploy). Este aqui faz uma
 * query trivial no banco de proposito, pra servir de alvo de monitoramento
 * externo (UptimeRobot etc) que mantem o Supabase ativo.
 */
@RestController
@RequiredArgsConstructor
public class PingController {

    private final UserRepository userRepository;

    @GetMapping("/api/ping")
    public String ping() {
        userRepository.count();
        return "ok";
    }
}
