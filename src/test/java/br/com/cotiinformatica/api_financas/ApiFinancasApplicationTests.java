package br.com.cotiinformatica.api_financas;

import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiFinancasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve criar uma categoria com sucesso.")
    public void criarCategoriaTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("Categoria teste");

        //ACT (Executar o endpoint POST /api/v1/categorias/criar)
        var result = mockMvc.perform(
                        post("/api/v1/categorias/criar") //Requisição POST para a API
                                .contentType("application/json") //Formato dos dados (JSON)
                                .content(objectMapper.writeValueAsString(request))) //Dados enviados
                .andExpect(status().isCreated()) //Esperando retorno HTTP 201
                .andReturn(); //Capturando os dados da resposta

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var response = objectMapper.readValue(jsonContent, CategoriaResponse.class);

        //ASSERT: O ID da categoria deve vir preenchido com um UUID aleatório
        assertNotNull(response.id());

        //ASSERT: O nome da categoria retornado deve ser igual ao enviado no cadastro
        assertEquals(request.nome(), response.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se o nome da categoria estiver vazio.")
    public void validarNomeDaCategoriaObrigatorioTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("");

        //ACT (Executar o endpoint POST /api/v1/categorias/criar)
        var result = mockMvc.perform(
                        post("/api/v1/categorias/criar") //Requisição POST para a API
                                .contentType("application/json") //Formato dos dados (JSON)
                                .content(objectMapper.writeValueAsString(request))) //Dados enviados
                .andExpect(status().isBadRequest()) //Esperando retorno HTTP 400
                .andReturn(); //Capturando os dados da resposta

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O nome da categoria é obrigatório."));
    }

    @Test
    @DisplayName("Deve retornar erro se o nome da categoria tiver menos de 6 caracteres.")
    public void validarNomeDaCategoriaMinimoDeCaracteresTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("Teste");

        //ACT (Executar o endpoint POST /api/v1/categorias/criar)
        var result = mockMvc.perform(
                        post("/api/v1/categorias/criar") //Requisição POST para a API
                                .contentType("application/json") //Formato dos dados (JSON)
                                .content(objectMapper.writeValueAsString(request))) //Dados enviados
                .andExpect(status().isBadRequest()) //Esperando retorno HTTP 400
                .andReturn(); //Capturando os dados da resposta

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O nome da categoria deve ter pelo menos 6 caracteres."));
    }

    @Test
    @DisplayName("Deve alterar uma categoria com sucesso.")
    public void alterarCategoriaTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var requestCriar = new CategoriaRequest("Categoria Original");

        //Criar a categoria para ter um ID válido
        var resultCriar = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCriar)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContentCriar = resultCriar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseCriar = objectMapper.readValue(jsonContentCriar, CategoriaResponse.class);

        //ACT (Executar o endpoint PUT /api/v1/categorias/alterar/{id})
        var requestAlterar = new CategoriaRequest("Categoria Alterada");
        var resultAlterar = mockMvc.perform(
                        put("/api/v1/categorias/alterar/" + responseCriar.id())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestAlterar)))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContentAlterar = resultAlterar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseAlterar = objectMapper.readValue(jsonContentAlterar, CategoriaResponse.class);

        assertEquals(responseCriar.id(), responseAlterar.id());
        assertEquals(requestAlterar.nome(), responseAlterar.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se tentar alterar categoria inexistente.")
    public void alterarCategoriaInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request = new CategoriaRequest("Categoria Teste");
        var idInexistente = java.util.UUID.randomUUID();

        //ACT (Executar o endpoint PUT /api/v1/categorias/alterar/{id})
        var result = mockMvc.perform(
                        put("/api/v1/categorias/alterar/" + idInexistente)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada."));
    }

    @Test
    @DisplayName("Deve excluir uma categoria com sucesso.")
    public void excluirCategoriaTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var requestCriar = new CategoriaRequest("Categoria Para Excluir");

        //Criar a categoria para ter um ID válido
        var resultCriar = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCriar)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContentCriar = resultCriar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseCriar = objectMapper.readValue(jsonContentCriar, CategoriaResponse.class);

        //ACT (Executar o endpoint DELETE /api/v1/categorias/excluir/{id})
        var resultExcluir = mockMvc.perform(
                        delete("/api/v1/categorias/excluir/" + responseCriar.id()))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContentExcluir = resultExcluir.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseExcluir = objectMapper.readValue(jsonContentExcluir, CategoriaResponse.class);

        assertEquals(responseCriar.id(), responseExcluir.id());
        assertEquals(responseCriar.nome(), responseExcluir.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se tentar excluir categoria inexistente.")
    public void excluirCategoriaInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var idInexistente = java.util.UUID.randomUUID();

        //ACT (Executar o endpoint DELETE /api/v1/categorias/excluir/{id})
        var result = mockMvc.perform(
                        delete("/api/v1/categorias/excluir/" + idInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada."));
    }

    @Test
    @DisplayName("Deve obter uma categoria por ID com sucesso.")
    public void obterCategoriaPorIdTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var requestCriar = new CategoriaRequest("Categoria Para Consultar");

        //Criar a categoria para ter um ID válido
        var resultCriar = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCriar)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContentCriar = resultCriar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseCriar = objectMapper.readValue(jsonContentCriar, CategoriaResponse.class);

        //ACT (Executar o endpoint GET /api/v1/categorias/obter/{id})
        var resultObter = mockMvc.perform(
                        get("/api/v1/categorias/obter/" + responseCriar.id()))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContentObter = resultObter.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseObter = objectMapper.readValue(jsonContentObter, CategoriaResponse.class);

        assertEquals(responseCriar.id(), responseObter.id());
        assertEquals(responseCriar.nome(), responseObter.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se tentar obter categoria inexistente.")
    public void obterCategoriaPorIdInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var idInexistente = java.util.UUID.randomUUID();

        //ACT (Executar o endpoint GET /api/v1/categorias/obter/{id})
        var result = mockMvc.perform(
                        get("/api/v1/categorias/obter/" + idInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada."));
    }

    @Test
    @DisplayName("Deve consultar todas as categorias com sucesso.")
    public void consultarCategoriasTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var request1 = new CategoriaRequest("Primeira Categoria");
        var request2 = new CategoriaRequest("Segunda Categoria");

        //Criar primeira categoria
        mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        //Criar segunda categoria
        mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        //ACT (Executar o endpoint GET /api/v1/categorias/consultar)
        var result = mockMvc.perform(
                        get("/api/v1/categorias/consultar"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var categorias = objectMapper.readValue(jsonContent, CategoriaResponse[].class);

        assertNotNull(categorias);
        assertTrue(categorias.length >= 2);
    }

    // ============= TESTES DE MOVIMENTAÇÕES =============

    private java.util.UUID criarCategoriaTeste(String nome) throws Exception {
        var requestCategoria = new CategoriaRequest(nome);
        var resultCategoria = mockMvc.perform(
                        post("/api/v1/categorias/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCategoria)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContent = resultCategoria.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var categoria = objectMapper.readValue(jsonContent, CategoriaResponse.class);
        return categoria.id();
    }

    @Test
    @DisplayName("Deve criar uma movimentação com sucesso.")
    public void criarMovimentacaoTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request = new MovimentacaoRequest(
                "Movimentação Teste",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );

        //ACT (Executar o endpoint POST /api/v1/movimentacoes/criar)
        var result = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var response = objectMapper.readValue(jsonContent, MovimentacaoResponse.class);

        assertNotNull(response.id());
        assertEquals(request.nome(), response.nome());
        assertEquals(request.valor(), response.valor());
        assertEquals(request.tipo(), response.tipo());
    }

    @Test
    @DisplayName("Deve retornar erro se o nome da movimentação estiver vazio.")
    public void validarNomeDaMovimentacaoObrigatorioTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request = new MovimentacaoRequest(
                "",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );

        //ACT (Executar o endpoint POST /api/v1/movimentacoes/criar)
        var result = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O nome da movimentação é obrigatório."));
    }

    @Test
    @DisplayName("Deve retornar erro se o nome da movimentação tiver menos de 6 caracteres.")
    public void validarNomeDaMovimentacaoMinimoDeCaracteresTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request = new MovimentacaoRequest(
                "Teste",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );

        //ACT (Executar o endpoint POST /api/v1/movimentacoes/criar)
        var result = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O nome da movimentação deve ter pelo menos 6 caracteres."));
    }

    @Test
    @DisplayName("Deve retornar erro se o valor da movimentação for menor ou igual a zero.")
    public void validarValorDaMovimentacaoMaiorQueZeroTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request = new MovimentacaoRequest(
                "Movimentação Teste",
                LocalDate.now(),
                0.0,
                "RECEITA",
                categoriaId
        );

        //ACT (Executar o endpoint POST /api/v1/movimentacoes/criar)
        var result = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O valor da movimentação deve ser maior do que zero."));
    }

    @Test
    @DisplayName("Deve retornar erro se o tipo da movimentação for inválido.")
    public void validarTipoDaMovimentacaoTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request = new MovimentacaoRequest(
                "Movimentação Teste",
                LocalDate.now(),
                100.50,
                "INVALIDO",
                categoriaId
        );

        //ACT (Executar o endpoint POST /api/v1/movimentacoes/criar)
        var result = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("O tipo da movimentação deve ser RECEITA ou DESPESA."));
    }

    @Test
    @DisplayName("Deve retornar erro se a categoria não existir.")
    public void validarCategoriaInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaIdInexistente = java.util.UUID.randomUUID();
        var request = new MovimentacaoRequest(
                "Movimentação Teste",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaIdInexistente
        );

        //ACT (Executar o endpoint POST /api/v1/movimentacoes/criar)
        var result = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Categoria não encontrada."));
    }

    @Test
    @DisplayName("Deve alterar uma movimentação com sucesso.")
    public void alterarMovimentacaoTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var requestCriar = new MovimentacaoRequest(
                "Movimentação Original",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );

        //Criar a movimentação para ter um ID válido
        var resultCriar = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCriar)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContentCriar = resultCriar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseCriar = objectMapper.readValue(jsonContentCriar, MovimentacaoResponse.class);

        //ACT (Executar o endpoint PUT /api/v1/movimentacoes/alterar/{id})
        var requestAlterar = new MovimentacaoRequest(
                "Movimentação Alterada",
                LocalDate.now().minusDays(1),
                150.75,
                "DESPESA",
                categoriaId
        );

        var resultAlterar = mockMvc.perform(
                        put("/api/v1/movimentacoes/alterar/" + responseCriar.id())
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestAlterar)))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContentAlterar = resultAlterar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseAlterar = objectMapper.readValue(jsonContentAlterar, MovimentacaoResponse.class);

        assertEquals(responseCriar.id(), responseAlterar.id());
        assertEquals(requestAlterar.nome(), responseAlterar.nome());
        assertEquals(requestAlterar.valor(), responseAlterar.valor());
        assertEquals(requestAlterar.tipo(), responseAlterar.tipo());
    }

    @Test
    @DisplayName("Deve retornar erro se tentar alterar movimentação inexistente.")
    public void alterarMovimentacaoInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request = new MovimentacaoRequest(
                "Movimentação Teste",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );
        var idInexistente = java.util.UUID.randomUUID();

        //ACT (Executar o endpoint PUT /api/v1/movimentacoes/alterar/{id})
        var result = mockMvc.perform(
                        put("/api/v1/movimentacoes/alterar/" + idInexistente)
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Movimentação não encontrada."));
    }

    @Test
    @DisplayName("Deve excluir uma movimentação com sucesso.")
    public void excluirMovimentacaoTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var requestCriar = new MovimentacaoRequest(
                "Movimentação Para Excluir",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );

        //Criar a movimentação para ter um ID válido
        var resultCriar = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCriar)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContentCriar = resultCriar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseCriar = objectMapper.readValue(jsonContentCriar, MovimentacaoResponse.class);

        //ACT (Executar o endpoint DELETE /api/v1/movimentacoes/excluir/{id})
        var resultExcluir = mockMvc.perform(
                        delete("/api/v1/movimentacoes/excluir/" + responseCriar.id()))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContentExcluir = resultExcluir.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseExcluir = objectMapper.readValue(jsonContentExcluir, MovimentacaoResponse.class);

        assertEquals(responseCriar.id(), responseExcluir.id());
        assertEquals(responseCriar.nome(), responseExcluir.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se tentar excluir movimentação inexistente.")
    public void excluirMovimentacaoInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var idInexistente = java.util.UUID.randomUUID();

        //ACT (Executar o endpoint DELETE /api/v1/movimentacoes/excluir/{id})
        var result = mockMvc.perform(
                        delete("/api/v1/movimentacoes/excluir/" + idInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Movimentação não encontrada."));
    }

    @Test
    @DisplayName("Deve obter uma movimentação por ID com sucesso.")
    public void obterMovimentacaoPorIdTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var requestCriar = new MovimentacaoRequest(
                "Movimentação Para Consultar",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );

        //Criar a movimentação para ter um ID válido
        var resultCriar = mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(requestCriar)))
                .andExpect(status().isCreated())
                .andReturn();

        var jsonContentCriar = resultCriar.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseCriar = objectMapper.readValue(jsonContentCriar, MovimentacaoResponse.class);

        //ACT (Executar o endpoint GET /api/v1/movimentacoes/obter/{id})
        var resultObter = mockMvc.perform(
                        get("/api/v1/movimentacoes/obter/" + responseCriar.id()))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContentObter = resultObter.getResponse().getContentAsString(StandardCharsets.UTF_8);
        var responseObter = objectMapper.readValue(jsonContentObter, MovimentacaoResponse.class);

        assertEquals(responseCriar.id(), responseObter.id());
        assertEquals(responseCriar.nome(), responseObter.nome());
    }

    @Test
    @DisplayName("Deve retornar erro se tentar obter movimentação inexistente.")
    public void obterMovimentacaoPorIdInexistenteTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var idInexistente = java.util.UUID.randomUUID();

        //ACT (Executar o endpoint GET /api/v1/movimentacoes/obter/{id})
        var result = mockMvc.perform(
                        get("/api/v1/movimentacoes/obter/" + idInexistente))
                .andExpect(status().isNotFound())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("Movimentação não encontrada."));
    }

    @Test
    @DisplayName("Deve consultar movimentações por período com sucesso.")
    public void consultarMovimentacoesPorPeriodoTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var categoriaId = criarCategoriaTeste("Categoria Movimentação");
        var request1 = new MovimentacaoRequest(
                "Primeira Movimentação",
                LocalDate.now(),
                100.50,
                "RECEITA",
                categoriaId
        );
        var request2 = new MovimentacaoRequest(
                "Segunda Movimentação",
                LocalDate.now().minusDays(1),
                50.25,
                "DESPESA",
                categoriaId
        );

        //Criar primeira movimentação
        mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        //Criar segunda movimentação
        mockMvc.perform(
                        post("/api/v1/movimentacoes/criar")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        //ACT (Executar o endpoint GET /api/v1/movimentacoes/consultar)
        var dataInicio = LocalDate.now().minusDays(5);
        var dataFim = LocalDate.now().plusDays(5);
        var result = mockMvc.perform(
                        get("/api/v1/movimentacoes/consultar")
                                .param("dataInicio", dataInicio.toString())
                                .param("dataFim", dataFim.toString())
                                .param("pageIndex", "0")
                                .param("pageSize", "25"))
                .andExpect(status().isOk())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertNotNull(jsonContent);
        assertTrue(jsonContent.contains("\"content\""));
    }

    @Test
    @DisplayName("Deve retornar erro se a data de início for maior que a data de fim.")
    public void validarPeriodoDasMovimentacoesTest() throws Exception {

        //ARRANGE (Preparar os dados do teste)
        var dataInicio = LocalDate.now().plusDays(5);
        var dataFim = LocalDate.now().minusDays(5);

        //ACT (Executar o endpoint GET /api/v1/movimentacoes/consultar)
        var result = mockMvc.perform(
                        get("/api/v1/movimentacoes/consultar")
                                .param("dataInicio", dataInicio.toString())
                                .param("dataFim", dataFim.toString())
                                .param("pageIndex", "0")
                                .param("pageSize", "25"))
                .andExpect(status().isBadRequest())
                .andReturn();

        //ASSERT (verificar o resultado do teste)
        var jsonContent = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(jsonContent.contains("A data de início não pode ser maior do que a data de fim."));
    }
}
