package br.com.cotiinformatica.api_financas.exceptions;

public class EnvioRelatorioException extends RuntimeException {

    public EnvioRelatorioException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
