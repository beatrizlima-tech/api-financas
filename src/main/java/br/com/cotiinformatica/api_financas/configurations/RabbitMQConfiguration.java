package br.com.cotiinformatica.api_financas.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String RELATORIOS_QUEUE =
            "relatorios-movimentacoes";

    public static final String RELATORIOS_DLQ =
            "relatorios-movimentacoes.dlq";

    @Bean("relatoriosQueue")
    Queue relatoriosQueue() {

        return QueueBuilder
                .durable(RELATORIOS_QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(RELATORIOS_DLQ)
                .build();
    }

    @Bean("relatoriosDeadLetterQueue")
    Queue relatoriosDeadLetterQueue() {

        return QueueBuilder
                .durable(RELATORIOS_DLQ)
                .build();
    }
}