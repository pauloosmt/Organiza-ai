package com.organizaai.infra.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityStartupCheckTest {

    private static final String SEGREDO_DEV_DEFAULT = "change-this-development-only-secret-must-be-long-enough";

    private final SecurityStartupCheck check = new SecurityStartupCheck();

    @Test
    void recusaSubirComCookieSecureESegredoDefault() {
        assertThrows(IllegalStateException.class, () -> check.validar(true, SEGREDO_DEV_DEFAULT));
    }

    @Test
    void permiteSubirComCookieSecureESegredoReal() {
        assertDoesNotThrow(() -> check.validar(true, "um-segredo-real-de-producao-bem-longo"));
    }

    @Test
    void permiteSubirComSegredoDefaultForaDeProducao() {
        assertDoesNotThrow(() -> check.validar(false, SEGREDO_DEV_DEFAULT));
    }
}
