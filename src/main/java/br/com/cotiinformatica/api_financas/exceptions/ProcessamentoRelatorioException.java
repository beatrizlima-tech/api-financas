package br.com.cotiinformatica.api_financas.exceptions;

public class ProcessamentoRelatorioException
        extends RuntimeException {

    public ProcessamentoRelatorioException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}