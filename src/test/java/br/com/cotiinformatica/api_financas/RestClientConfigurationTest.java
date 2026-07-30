package br.com.cotiinformatica.api_financas;

import br.com.cotiinformatica.api_financas.configurations.RestClientConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RestClientConfigurationTest {

    private static final String BASE_URL =
            "http://localhost:8084";

    private static final String API_KEY_TEST =
            "chave-interna-ficticia-para-testes";

    private final RestClientConfiguration configuration =
            new RestClientConfiguration();

    @Test
    @DisplayName("Deve enviar a chave interna para a API de agentes.")
    void deveEnviarChaveInternaTest() {

        var builder = RestClient.builder();

        var server = MockRestServiceServer
                .bindTo(builder)
                .build();

        var restClient = configuration.restClient(
                builder,
                BASE_URL,
                API_KEY_TEST
        );

        server.expect(
                        requestTo(
                                BASE_URL + "/api/relatorios"
                        )
                )
                .andExpect(
                        header(
                                "X-Internal-Api-Key",
                                API_KEY_TEST
                        )
                )
                .andRespond(withSuccess());

        restClient.post()
                .uri("/api/relatorios")
                .retrieve()
                .toBodilessEntity();

        server.verify();
    }

    @Test
    @DisplayName("Deve rejeitar configuração sem a chave interna.")
    void deveRejeitarChaveInternaVaziaTest() {

        var builder = RestClient.builder();

        assertThrows(
                IllegalStateException.class,
                () -> configuration.restClient(
                        builder,
                        BASE_URL,
                        " "
                )
        );
    }
}