package com.organizaai.controller;

import com.organizaai.data.dto.request.LoginRequest;
import com.organizaai.data.dto.request.ReenviarCodigoRequest;
import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.VerificarEmailRequest;
import com.organizaai.data.entity.User;
import com.organizaai.infra.security.JwtService;
import com.organizaai.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerCreatesUserWithoutExposingPassword() throws Exception {
        RegisterRequest request = new RegisterRequest("Maria", "maria@example.com", "senha1234");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void registerWithDuplicateEmailIsRejected() throws Exception {
        RegisterRequest request = new RegisterRequest("Joao", "joao@example.com", "senha1234");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void registerLeavesUserUnverifiedWithCodeSet() throws Exception {
        registerUser("beatriz-uc1@example.com", "senha1234");

        User user = userRepository.findByEmail("beatriz-uc1@example.com").orElseThrow();
        assertFalse(user.isEmailVerificado());
        assertNotNull(user.getCodigoVerificacao());
        assertNotNull(user.getCodigoVerificacaoExpiraEm());
    }

    @Test
    void loginBeforeVerifyingEmailIsForbidden() throws Exception {
        registerUser("rafael-uc1@example.com", "senha1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("rafael-uc1@example.com", "senha1234"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void verifyingWithCorrectCodeAllowsLogin() throws Exception {
        registerUser("camila-uc1@example.com", "senha1234");
        verificarEmail("camila-uc1@example.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("camila-uc1@example.com", "senha1234"))))
                .andExpect(status().isOk());
    }

    @Test
    void verifyingWithWrongCodeIsRejected() throws Exception {
        registerUser("thiago-uc1@example.com", "senha1234");

        mockMvc.perform(post("/api/auth/verificar-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new VerificarEmailRequest("thiago-uc1@example.com", "000000"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resendingCodeGeneratesNewCode() throws Exception {
        registerUser("larissa-uc1@example.com", "senha1234");
        String codigoOriginal = userRepository.findByEmail("larissa-uc1@example.com").orElseThrow().getCodigoVerificacao();

        mockMvc.perform(post("/api/auth/reenviar-codigo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ReenviarCodigoRequest("larissa-uc1@example.com"))))
                .andExpect(status().isNoContent());

        String codigoNovo = userRepository.findByEmail("larissa-uc1@example.com").orElseThrow().getCodigoVerificacao();
        assertNotEquals(codigoOriginal, codigoNovo);
    }

    @Test
    void loginWithCorrectCredentialsSetsAuthCookie() throws Exception {
        registerUser("ana-uc1@example.com", "senha1234");
        verificarEmail("ana-uc1@example.com");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("ana-uc1@example.com", "senha1234"))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie(JwtService.COOKIE_NAME);
        assertNotNull(cookie);
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.getValue().isBlank());
    }

    @Test
    void loginWithWrongPasswordIsRejected() throws Exception {
        registerUser("carlos-uc1@example.com", "senha1234");
        verificarEmail("carlos-uc1@example.com");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("carlos-uc1@example.com", "senha-errada"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithoutCookieIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedRouteWithValidCookieReturnsCurrentUser() throws Exception {
        registerUser("paula-uc1@example.com", "senha1234");
        verificarEmail("paula-uc1@example.com");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("paula-uc1@example.com", "senha1234"))))
                .andReturn();

        Cookie cookie = loginResult.getResponse().getCookie(JwtService.COOKIE_NAME);

        mockMvc.perform(get("/api/users/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("paula-uc1@example.com"));
    }

    @Test
    void authenticatedRequestRenewsCookie() throws Exception {
        registerUser("vinicius-uc1@example.com", "senha1234");
        verificarEmail("vinicius-uc1@example.com");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("vinicius-uc1@example.com", "senha1234"))))
                .andReturn();

        Cookie cookieOriginal = loginResult.getResponse().getCookie(JwtService.COOKIE_NAME);
        Thread.sleep(1000);

        MvcResult resultAutenticado = mockMvc.perform(get("/api/users/me").cookie(cookieOriginal))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookieRenovado = resultAutenticado.getResponse().getCookie(JwtService.COOKIE_NAME);
        assertNotNull(cookieRenovado);
        assertNotEquals(cookieOriginal.getValue(), cookieRenovado.getValue());
    }

    private void registerUser(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("Usuario Teste", email, password))))
                .andExpect(status().isCreated());
    }

    private void verificarEmail(String email) throws Exception {
        String codigo = userRepository.findByEmail(email).orElseThrow().getCodigoVerificacao();
        mockMvc.perform(post("/api/auth/verificar-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VerificarEmailRequest(email, codigo))))
                .andExpect(status().isNoContent());
    }
}
