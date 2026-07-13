package com.organizaai.controller;

import com.organizaai.data.dto.request.AtualizarTemaRequest;
import com.organizaai.data.dto.request.ConfirmarTrocaSenhaRequest;
import com.organizaai.data.dto.request.CreateAvaliacaoRequest;
import com.organizaai.data.dto.request.CreateDisciplinaRequest;
import com.organizaai.data.dto.request.CreateGradeBlocoRequest;
import com.organizaai.data.dto.request.CreatePeriodoRequest;
import com.organizaai.data.dto.request.ExcluirContaRequest;
import com.organizaai.data.dto.request.LoginRequest;
import com.organizaai.data.dto.request.RegisterRequest;
import com.organizaai.data.dto.request.TrocarSenhaRequest;
import com.organizaai.data.dto.request.UpdateUserRequest;
import com.organizaai.data.dto.request.VerificarEmailRequest;
import com.organizaai.data.entity.TipoAvaliacao;
import com.organizaai.repository.AvaliacaoRepository;
import com.organizaai.repository.DisciplinaRepository;
import com.organizaai.repository.GradeBlocoRepository;
import com.organizaai.repository.PeriodoRepository;
import com.organizaai.repository.PremiumInterestRepository;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PeriodoRepository periodoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private GradeBlocoRepository gradeBlocoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PremiumInterestRepository premiumInterestRepository;

    @Test
    void editarNomeAtualizaPerfil() throws Exception {
        Cookie cookie = registerAndLogin("nome-perfil@example.com");

        mockMvc.perform(put("/api/users/me").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateUserRequest("Nome Novo"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nome Novo"));
    }

    @Test
    void iniciarTrocaDeSenhaNaoAlteraSenhaAtual() throws Exception {
        Cookie cookie = registerAndLogin("iniciar-troca@example.com");

        mockMvc.perform(post("/api/users/me/senha").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TrocarSenhaRequest("senhaNova123"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("iniciar-troca@example.com", "senha1234"))))
                .andExpect(status().isOk());
    }

    @Test
    void confirmarTrocaDeSenhaComCodigoCertoTrocaSenha() throws Exception {
        Cookie cookie = registerAndLogin("confirmar-troca@example.com");

        mockMvc.perform(post("/api/users/me/senha").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TrocarSenhaRequest("senhaNova123"))))
                .andExpect(status().isNoContent());

        String codigo = userRepository.findByEmail("confirmar-troca@example.com").orElseThrow().getCodigoTrocaSenha();

        mockMvc.perform(post("/api/users/me/senha/confirmar").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConfirmarTrocaSenhaRequest(codigo))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("confirmar-troca@example.com", "senha1234"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("confirmar-troca@example.com", "senhaNova123"))))
                .andExpect(status().isOk());
    }

    @Test
    void confirmarTrocaDeSenhaComCodigoErradoERejeitada() throws Exception {
        Cookie cookie = registerAndLogin("codigo-errado-troca@example.com");

        mockMvc.perform(post("/api/users/me/senha").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TrocarSenhaRequest("senhaNova123"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/users/me/senha/confirmar").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ConfirmarTrocaSenhaRequest("000000"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reenviarCodigoDeTrocaDeSenhaGeraCodigoNovo() throws Exception {
        Cookie cookie = registerAndLogin("reenviar-troca@example.com");

        mockMvc.perform(post("/api/users/me/senha").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TrocarSenhaRequest("senhaNova123"))))
                .andExpect(status().isNoContent());

        String codigoOriginal = userRepository.findByEmail("reenviar-troca@example.com").orElseThrow().getCodigoTrocaSenha();

        mockMvc.perform(post("/api/users/me/senha/reenviar").cookie(cookie))
                .andExpect(status().isNoContent());

        String codigoNovo = userRepository.findByEmail("reenviar-troca@example.com").orElseThrow().getCodigoTrocaSenha();
        assertNotEquals(codigoOriginal, codigoNovo);
    }

    @Test
    void atualizarTemaPersisteEVoltaNoUserResponse() throws Exception {
        Cookie cookie = registerAndLogin("tema-perfil@example.com");

        mockMvc.perform(put("/api/users/me/tema").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AtualizarTemaRequest("dark"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tema").value("dark"));

        mockMvc.perform(get("/api/users/me").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tema").value("dark"));
    }

    @Test
    void excluirContaComSenhaErradaNaoApagaNada() throws Exception {
        Cookie cookie = registerAndLogin("senha-errada-exclusao@example.com");

        mockMvc.perform(delete("/api/users/me").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExcluirContaRequest("senha-errada"))))
                .andExpect(status().isBadRequest());

        assertTrue(userRepository.findByEmail("senha-errada-exclusao@example.com").isPresent());
    }

    @Test
    void excluirContaComSenhaCertaApagaTudoEExpiraCookie() throws Exception {
        Cookie cookie = registerAndLogin("excluir-conta@example.com");
        UUID userId = userRepository.findByEmail("excluir-conta@example.com").orElseThrow().getId();

        UUID periodoId = criarPeriodo(cookie, 2031, 1);
        UUID disciplinaId = criarDisciplina(cookie, periodoId, "Disciplina a apagar");
        criarBloco(cookie, disciplinaId, 1, 8, 9);
        criarAvaliacao(cookie, disciplinaId);
        mockMvc.perform(post("/api/premium/interesse").cookie(cookie))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(delete("/api/users/me").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ExcluirContaRequest("senha1234"))))
                .andExpect(status().isNoContent())
                .andReturn();

        // O JwtAuthenticationFilter renova o cookie em toda request autenticada (roda antes do
        // controller), então a resposta tem 2 Set-Cookie pra "organizaai_token": a renovação do
        // filtro e a expiração do controller. Navegadores aplicam em ordem e o último Set-Cookie
        // vence — pegamos o último aqui pra refletir o comportamento real.
        Cookie[] cookiesComMesmoNome = java.util.Arrays.stream(result.getResponse().getCookies())
                .filter(c -> c.getName().equals("organizaai_token"))
                .toArray(Cookie[]::new);
        Cookie cookieExpirado = cookiesComMesmoNome[cookiesComMesmoNome.length - 1];
        assertEquals(0, cookieExpirado.getMaxAge());

        assertTrue(userRepository.findById(userId).isEmpty());
        assertTrue(periodoRepository.findById(periodoId).isEmpty());
        assertTrue(disciplinaRepository.findById(disciplinaId).isEmpty());
        assertTrue(gradeBlocoRepository.findByUserId(userId).isEmpty());
        assertTrue(avaliacaoRepository.findByDisciplinaIdIn(java.util.List.of(disciplinaId)).isEmpty());
        assertTrue(premiumInterestRepository.findByUserId(userId).isEmpty());

        mockMvc.perform(get("/api/users/me").cookie(cookie))
                .andExpect(status().isUnauthorized());
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

    private void criarBloco(Cookie cookie, UUID disciplinaId, int diaSemana, int horaInicio, int horaFim) throws Exception {
        mockMvc.perform(post("/api/grade/blocos").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateGradeBlocoRequest(disciplinaId, diaSemana, horaInicio, horaFim))))
                .andExpect(status().isCreated());
    }

    private void criarAvaliacao(Cookie cookie, UUID disciplinaId) throws Exception {
        mockMvc.perform(post("/api/avaliacoes").cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateAvaliacaoRequest(
                                disciplinaId, "Prova 1", TipoAvaliacao.PROVA, LocalDate.now().plusDays(7), 10.0))))
                .andExpect(status().isCreated());
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
