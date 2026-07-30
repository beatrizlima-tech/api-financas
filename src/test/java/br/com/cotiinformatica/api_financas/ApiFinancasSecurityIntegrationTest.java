package br.com.cotiinformatica.api_financas;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiFinancasSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarUnauthorizedQuandoTokenNaoForInformado()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/categorias/consultar")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void devePermitirAcessoQuandoTokenForValido()
            throws Exception {

        var token = gerarTokenValido(
                UUID.randomUUID()
        );

        mockMvc.perform(
                        get("/api/v1/categorias/consultar")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + token
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void devePermitirAcessoAoHealthCheckSemToken()
            throws Exception {

        mockMvc.perform(
                        get("/actuator/health")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("UP"));
    }

    private String gerarTokenValido(
            UUID usuarioId
    ) {

        var encoder = NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();

        var emitidoEm = Instant.now();

        var claims = JwtClaimsSet.builder()
                .issuer("api-autenticacao")
                .subject(usuarioId.toString())
                .issuedAt(emitidoEm)
                .expiresAt(
                        emitidoEm.plus(
                                Duration.ofHours(1)
                        )
                )
                .claim(
                        "email",
                        usuarioId + "@teste.local"
                )
                .claim("perfil", "Operador")
                .build();

        var header = JwsHeader
                .with(MacAlgorithm.HS256)
                .type("JWT")
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(
                        header,
                        claims
                )
        ).getTokenValue();
    }

    @Test
    void naoDevePermitirAcessoACategoriaDeOutroUsuario() throws Exception {

        var tokenProprietario = gerarTokenValido(UUID.randomUUID());

        var tokenOutroUsuario = gerarTokenValido(UUID.randomUUID());

        var resultadoCriacao = mockMvc.perform(
                post("/api/v1/categorias/criar")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + tokenProprietario
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                "nome": "Categoria privada"
                                }
                                """)
        )
                .andExpect(status().isCreated())
                .andReturn();

        var json = resultadoCriacao.getResponse().getContentAsString(StandardCharsets.UTF_8);

        var categoriaId = objectMapper
                .readTree(json)
                .get("id")
                .asText();

        mockMvc.perform(
                        get("/api/v1/categorias/obter/" + categoriaId)
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenOutroUsuario
                                )
                )
                .andExpect(status().isNotFound());

    }

    @Test
    void naoDevePermitirAcessoAMovimentacaoDeOutroUsuario()
            throws Exception {

        var tokenProprietario =
                gerarTokenValido(UUID.randomUUID());

        var tokenOutroUsuario =
                gerarTokenValido(UUID.randomUUID());

        var resultadoCategoria = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenProprietario
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "nome": "Categoria privada"
                                    }
                                    """)
                )
                .andExpect(status().isCreated())
                .andReturn();

        var categoriaId = objectMapper
                .readTree(
                        resultadoCategoria
                                .getResponse()
                                .getContentAsString(
                                        StandardCharsets.UTF_8
                                )
                )
                .get("id")
                .asText();

        var resultadoMovimentacao = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenProprietario
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "nome": "Movimentação privada",
                                      "data": "2026-07-28",
                                      "valor": 150.00,
                                      "tipo": "DESPESA",
                                      "categoriaId": "%s"
                                    }
                                    """.formatted(categoriaId))
                )
                .andExpect(status().isCreated())
                .andReturn();

        var movimentacaoId = objectMapper
                .readTree(
                        resultadoMovimentacao
                                .getResponse()
                                .getContentAsString(
                                        StandardCharsets.UTF_8
                                )
                )
                .get("id")
                .asText();

        mockMvc.perform(
                        get(
                                "/api/v1/movimentacoes/obter/"
                                        + movimentacaoId
                        )
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + tokenOutroUsuario
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void devePermitirPreflightDaOrigemConfigurada()
            throws Exception {

        mockMvc.perform(
                        options("/api/v1/categorias/consultar")
                                .header(
                                        HttpHeaders.ORIGIN,
                                        "http://localhost:4200"
                                )
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                        "GET"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                                "http://localhost:4200"
                        )
                );
    }

    @Test
    void deveBloquearPreflightDeOrigemNaoConfigurada()
            throws Exception {

        mockMvc.perform(
                        options("/api/v1/categorias/consultar")
                                .header(
                                        HttpHeaders.ORIGIN,
                                        "https://origem-nao-permitida.example"
                                )
                                .header(
                                        HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD,
                                        "GET"
                                )
                )
                .andExpect(status().isForbidden());
    }

}