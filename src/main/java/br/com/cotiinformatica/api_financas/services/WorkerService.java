package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.configurations.RabbitMQConfiguration;
import br.com.cotiinformatica.api_financas.exceptions.EnvioRelatorioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
public class WorkerService {

    private final RestClient restClient;

    public WorkerService(RestClient restClient) {
        this.restClient = restClient;
    }

    @RabbitListener(queues = RabbitMQConfiguration.RELATORIOS_QUEUE)
    public void listener(@Payload String payload) {

        try {
            var result = restClient.post()
                    .uri("/api/relatorios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info(
                    "Relatório enviado com sucesso. Status HTTP: {}",
                    result.getStatusCode()
            );

        }
        catch (RestClientException exception) {

            log.error(
                    "Falha ao enviar o relatório para a API de agentes.",
                    exception
            );

            throw new EnvioRelatorioException(
                    "Não foi possível enviar o relatório para processamento.",
                    exception
            );
        }
    }
}