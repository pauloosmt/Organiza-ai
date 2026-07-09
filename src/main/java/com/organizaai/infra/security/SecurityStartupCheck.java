package com.organizaai.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SecurityStartupCheck implements ApplicationRunner {

    private static final String JWT_SECRET_DEV_DEFAULT = "change-this-development-only-secret-must-be-long-enough";

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Override
    public void run(ApplicationArguments args) {
        validar(cookieSecure, jwtSecret);
    }

    void validar(boolean cookieSecure, String jwtSecret) {
        if (cookieSecure && JWT_SECRET_DEV_DEFAULT.equals(jwtSecret)) {
            throw new IllegalStateException(
                    "app.cookie.secure=true indica ambiente de producao, mas JWT_SECRET ainda esta no valor "
                            + "default de desenvolvimento. Defina a variavel de ambiente JWT_SECRET com um "
                            + "segredo real antes de subir em producao."
            );
        }
    }
}
