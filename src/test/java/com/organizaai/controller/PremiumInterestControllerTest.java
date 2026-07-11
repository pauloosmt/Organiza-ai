package com.organizaai.controller;

import com.organizaai.data.dto.request.LoginRequest;
import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.VerificarEmailRequest;
import com.organizaai.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PremiumInterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void statusComecaNaoRegistrado() throws Exception {
        Cookie cookie = registerAndLogin("julia-premium@example.com");

        mockMvc.perform(get("/api/premium/interesse").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrado").value(false));
    }

    @Test
    void registrarInteresseFicaRegistrado() throws Exception {
        Cookie cookie = registerAndLogin("karina@example.com");

        mockMvc.perform(post("/api/premium/interesse").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrado").value(true));

        mockMvc.perform(get("/api/premium/interesse").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrado").value(true));
    }

    @Test
    void registrarInteresseDuasVezesEIdempotente() throws Exception {
        Cookie cookie = registerAndLogin("leandro@example.com");

        mockMvc.perform(post("/api/premium/interesse").cookie(cookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/premium/interesse").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrado").value(true));
    }

    private Cookie registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("Usuario Teste", email, "senha1234"))))
                .andExpect(status().isCreated());

        String codigo = userRepository.findByEmail(email).orElseThrow().getCodigoVerificacao();
        mockMvc.perform(post("/api/auth/verificar-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VerificarEmailRequest(email, codigo))))
                .andExpect(status().isNoContent());

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "senha1234"))))
                .andExpect(status().isOk())
                .andReturn();

        return loginResult.getResponse().getCookie("organizaai_token");
    }
}
