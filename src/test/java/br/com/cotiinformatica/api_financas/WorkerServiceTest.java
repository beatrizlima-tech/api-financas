package br.com.cotiinformatica.api_financas;

import br.com.cotiinformatica.api_financas.exceptions.EnvioRelatorioException;
import br.com.cotiinformatica.api_financas.services.WorkerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class WorkerServiceTest {

    private MockRestServiceServer mockServer;
    private WorkerService workerService;

    @BeforeEach
    void setUp() {

        var builder = RestClient.builder()
                .baseUrl("http://localhost:8084");

        mockServer = MockRestServiceServer
                .bindTo(builder)
                .build();

        workerService = new WorkerService(builder.build());
    }

    @Test
    @DisplayName("Deve enviar o relatório para a API de agentes.")
    void deveEnviarRelatorioParaApiDeAgentes() {

        mockServer.expect(
                        requestTo(
                                "http://localhost:8084/api/relatorios"
                        )
                )
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        workerService.listener("""
                {
                  "email": "usuario@email.com",
                  "movimentacoes": []
                }
                """);

        mockServer.verify();
    }

    @Test
    @DisplayName("Deve lançar exceção quando a API de agentes falhar.")
    void deveLancarExcecaoQuandoApiDeAgentesFalhar() {

        mockServer.expect(
                        requestTo(
                                "http://localhost:8084/api/relatorios"
                        )
                )
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        var exception = assertThrows(
                EnvioRelatorioException.class,
                () -> workerService.listener("{}")
        );

        assertEquals(
                "Não foi possível enviar o relatório para processamento.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        mockServer.verify();
    }
}