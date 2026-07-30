package br.com.cotiinformatica.api_financas.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    private static final String INTERNAL_API_KEY_HEADER =
            "X-Internal-Api-Key";

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient restClient(
            RestClient.Builder builder,
            @Value("${integrations.agentes-ia.base-url}")
            String baseUrl,
            @Value("${integrations.agentes-ia.api-key}")
            String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "A chave de acesso à API de agentes não foi configurada."
            );
        }

        return builder
                .baseUrl(baseUrl)
                .defaultHeader(
                        INTERNAL_API_KEY_HEADER,
                        apiKey
                )
                .build();
    }
}