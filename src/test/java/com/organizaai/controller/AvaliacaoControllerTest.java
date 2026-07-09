package com.organizaai.controller;

import com.organizaai.data.dto.request.CreateAvaliacaoRequest;
import com.organizaai.data.dto.request.CreateDisciplinaRequest;
import com.organizaai.data.dto.request.CreatePeriodoRequest;
import com.organizaai.data.dto.request.LoginRequest;
import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.UpdateAvaliacaoRequest;
import com.organizaai.data.entity.TipoAvaliacao;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void criaAvaliacaoSemNota() throws Exception {
        Cookie cookie = registerAndLogin("livia@example.com");
        UUID periodoId = criarPeriodo(cookie, 2030, 1);
        UUID disciplinaId = criarDisciplina(cookie, periodoId, "Cálculo I");

        criarAvaliacao(cookie, disciplinaId, "Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(10), 10.0)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Prova 1"))
                .andExpect(jsonPath("$.tipo").value("PROVA"))
                .andExpect(jsonPath("$.pontuacao").value(10.0))
                .andExpect(jsonPath("$.nota").doesNotExist());
    }

    @Test
    void listaAvaliacoesPorPeriodoIncluiDisciplinaEcor() throws Exception {
        Cookie cookie = registerAndLogin("marcelo@example.com");
        UUID periodoId = criarPeriodo(cookie, 2030, 1);
        UUID disciplinaId = criarDisciplina(cookie, periodoId, "Banco de Dados");

        criarAvaliacao(cookie, disciplinaId, "Trabalho 1", TipoAvaliacao.TRABALHO, LocalDate.now().plusDays(20), 30.0)
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/avaliacoes").param("periodoId", periodoId.toString()).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disciplinaNome").value("Banco de Dados"))
                .andExpect(jsonPath("$[0].corIndice").value(0));
    }

    @Test
    void lancaNotaEmAvaliacaoExistente() throws Exception {
        Cookie cookie = registerAndLogin("natalia@example.com");
        UUID periodoId = criarPeriodo(cookie, 2030, 1);
        UUID disciplinaId = criarDisciplina(cookie, periodoId, "Física I");

        MvcResult creation = criarAvaliacao(cookie, disciplinaId, "Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(5), 10.0)
                .andExpect(status().isCreated())
                .andReturn();
        UUID avaliacaoId = extrairId(creation);

        mockMvc.perform(patch("/api/avaliacoes/" + avaliacaoId).cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateAvaliacaoRequest("Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(5), 10.0, 8.5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(8.5));
    }

    @Test
    void removeAvaliacao() throws Exception {
        Cookie cookie = registerAndLogin("otavio@example.com");
        UUID periodoId = criarPeriodo(cookie, 2030, 1);
        UUID disciplinaId = criarDisciplina(cookie, periodoId, "Química");

        MvcResult creation = criarAvaliacao(cookie, disciplinaId, "Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(5), 10.0)
                .andExpect(status().isCreated())
                .andReturn();
        UUID avaliacaoId = extrairId(creation);

        mockMvc.perform(delete("/api/avaliacoes/" + avaliacaoId).cookie(cookie))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/api/avaliacoes").param("periodoId", periodoId.toString()).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    @Test
    void naoPermiteAlterarAvaliacaoDeOutroUsuario() throws Exception {
        Cookie cookieA = registerAndLogin("patricia@example.com");
        UUID periodoA = criarPeriodo(cookieA, 2030, 1);
        UUID disciplinaA = criarDisciplina(cookieA, periodoA, "Álgebra");
        MvcResult creation = criarAvaliacao(cookieA, disciplinaA, "Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(5), 10.0)
                .andExpect(status().isCreated())
                .andReturn();
        UUID avaliacaoId = extrairId(creation);

        Cookie cookieB = registerAndLogin("rafael@example.com");
        mockMvc.perform(delete("/api/avaliacoes/" + avaliacaoId).cookie(cookieB))
                .andExpect(status().isNotFound());
    }

    @Test
    void removerDisciplinaRemoveAvaliacoesDela() throws Exception {
        Cookie cookie = registerAndLogin("sabrina@example.com");
        UUID periodoId = criarPeriodo(cookie, 2030, 1);
        UUID disciplinaId = criarDisciplina(cookie, periodoId, "Programação II");
        criarAvaliacao(cookie, disciplinaId, "Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(5), 10.0)
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/disciplinas/" + disciplinaId).cookie(cookie))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/api/avaliacoes").param("periodoId", periodoId.toString()).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    private UUID criarPeriodo(Cookie cookie, int ano, int semestre) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/periodos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreatePeriodoRequest(ano, semestre))))
                .andExpect(status().isCreated())
                .andReturn();
        return extrairId(result);
    }

    private UUID criarDisciplina(Cookie cookie, UUID periodoId, String nome) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/disciplinas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDisciplinaRequest(nome, periodoId))))
                .andExpect(status().isCreated())
                .andReturn();
        return extrairId(result);
    }

    private ResultActions criarAvaliacao(
            Cookie cookie, UUID disciplinaId, String titulo, TipoAvaliacao tipo, LocalDate data, double pontuacao
    ) throws Exception {
        return mockMvc.perform(post("/api/avaliacoes").cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateAvaliacaoRequest(disciplinaId, titulo, tipo, data, pontuacao))));
    }

    private UUID extrairId(MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        return UUID.fromString(body.replaceAll(".*\"id\":\"([0-9a-fA-F-]+)\".*", "$1"));
    }

    private Cookie registerAndLogin(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RegisterRequest("Usuario Teste", email, "senha1234"))))
                .andExpect(status().isCreated());

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, "senha1234"))))
                .andExpect(status().isOk())
                .andReturn();

        return loginResult.getResponse().getCookie("organizaai_token");
    }
}
