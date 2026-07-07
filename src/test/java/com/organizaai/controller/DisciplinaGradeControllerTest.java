package com.organizaai.controller;

import com.organizaai.data.dto.request.AjustarFaltasRequest;
import com.organizaai.data.dto.request.CreateDisciplinaRequest;
import com.organizaai.data.dto.request.CreateGradeBlocoRequest;
import com.organizaai.data.dto.request.LoginRequest;
import com.organizaai.data.dto.request.RegisterRequest;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DisciplinaGradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createDisciplinaStartsWithZeroCreditosEFaltas() throws Exception {
        Cookie cookie = registerAndLogin("beatriz@example.com");

        mockMvc.perform(post("/api/disciplinas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDisciplinaRequest("Cálculo I"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Cálculo I"))
                .andExpect(jsonPath("$.creditos").value(0))
                .andExpect(jsonPath("$.faltas").value(0));
    }

    @Test
    void alocarBlocosNaGradeAumentaCreditosDaDisciplina() throws Exception {
        Cookie cookie = registerAndLogin("bruno@example.com");
        UUID disciplinaId = criarDisciplina(cookie, "Física I");

        criarBloco(cookie, disciplinaId, 1, 19, 21).andExpect(status().isCreated());
        mockMvc.perform(get("/api/disciplinas").cookie(cookie))
                .andExpect(jsonPath("$[0].creditos").value(2));

        criarBloco(cookie, disciplinaId, 3, 19, 21).andExpect(status().isCreated());
        mockMvc.perform(get("/api/disciplinas").cookie(cookie))
                .andExpect(jsonPath("$[0].creditos").value(4));
    }

    @Test
    void alocarBlocoSobrepostoERejeitadoComConflito() throws Exception {
        Cookie cookie = registerAndLogin("carla@example.com");
        UUID disciplinaId = criarDisciplina(cookie, "Química");

        criarBloco(cookie, disciplinaId, 2, 19, 21).andExpect(status().isCreated());
        criarBloco(cookie, disciplinaId, 2, 20, 22).andExpect(status().isConflict());
    }

    @Test
    void ajustarFaltasNuncaFicaNegativo() throws Exception {
        Cookie cookie = registerAndLogin("duda@example.com");
        UUID disciplinaId = criarDisciplina(cookie, "Estatística");

        mockMvc.perform(patch("/api/disciplinas/" + disciplinaId + "/faltas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AjustarFaltasRequest(1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faltas").value(1));

        mockMvc.perform(patch("/api/disciplinas/" + disciplinaId + "/faltas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AjustarFaltasRequest(-1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faltas").value(0));

        mockMvc.perform(patch("/api/disciplinas/" + disciplinaId + "/faltas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AjustarFaltasRequest(-1))))
                .andExpect(status().is(422));
    }

    @Test
    void removerDisciplinaRemoveBlocosDaGrade() throws Exception {
        Cookie cookie = registerAndLogin("erika@example.com");
        UUID disciplinaId = criarDisciplina(cookie, "Programação");
        criarBloco(cookie, disciplinaId, 4, 8, 10).andExpect(status().isCreated());

        mockMvc.perform(delete("/api/disciplinas/" + disciplinaId).cookie(cookie))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/api/grade/blocos").cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("[]", result.getResponse().getContentAsString());
    }

    private UUID criarDisciplina(Cookie cookie, String nome) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/disciplinas").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateDisciplinaRequest(nome))))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return UUID.fromString(body.replaceAll(".*\"id\":\"([0-9a-fA-F-]+)\".*", "$1"));
    }

    private org.springframework.test.web.servlet.ResultActions criarBloco(
            Cookie cookie, UUID disciplinaId, int diaSemana, int horaInicio, int horaFim
    ) throws Exception {
        return mockMvc.perform(post("/api/grade/blocos").cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new CreateGradeBlocoRequest(disciplinaId, diaSemana, horaInicio, horaFim))));
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
