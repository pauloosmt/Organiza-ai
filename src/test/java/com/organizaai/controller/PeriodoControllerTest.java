package com.organizaai.controller;

import com.organizaai.data.dto.request.CreateDisciplinaRequest;
import com.organizaai.data.dto.request.CreatePeriodoRequest;
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
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PeriodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criaPeriodoComNomeNoFormatoAnoBarraSemestre() throws Exception {
        Cookie cookie = registerAndLogin("fabio@example.com");

        mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(2031, 2))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("2031/2"));
    }

    @Test
    void criarPeriodoDuplicadoERejeitado() throws Exception {
        Cookie cookie = registerAndLogin("gustavo@example.com");

        mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(2032, 1))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(2032, 1))))
                .andExpect(status().isConflict());
    }

    @Test
    void listaPeriodosDoMaisRecenteParaOMaisAntigo() throws Exception {
        Cookie cookie = registerAndLogin("helena@example.com");

        mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(2033, 1))))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(2033, 2))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/periodos").cookie(cookie))
                .andExpect(jsonPath("$[0].nome").value("2033/2"))
                .andExpect(jsonPath("$[1].nome").value("2033/1"));
    }

    @Test
    void disciplinasFicamIsoladasPorPeriodo() throws Exception {
        Cookie cookie = registerAndLogin("igor@example.com");
        UUID periodoA = criarPeriodo(cookie, 2034, 1);
        UUID periodoB = criarPeriodo(cookie, 2034, 2);

        mockMvc.perform(post("/api/disciplinas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDisciplinaRequest("Álgebra", periodoA))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/disciplinas").param("periodoId", periodoA.toString()).cookie(cookie))
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/disciplinas").param("periodoId", periodoB.toString()).cookie(cookie))
                .andExpect(jsonPath("$.length()").value(0));
    }

    private UUID criarPeriodo(Cookie cookie, int ano, int semestre) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(ano, semestre))))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return UUID.fromString(body.replaceAll(".*\"id\":\"([0-9a-fA-F-]+)\".*", "$1"));
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

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "senha1234"))))
                .andExpect(status().isOk())
                .andReturn();

        return loginResult.getResponse().getCookie("organizaai_token");
    }
}
