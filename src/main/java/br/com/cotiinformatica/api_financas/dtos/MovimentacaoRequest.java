package br.com.cotiinformatica.api_financas.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoRequest(
        String nome,
        LocalDate data,
        Double valor,
        String tipo,
        UUID categoriaId
) {
}
