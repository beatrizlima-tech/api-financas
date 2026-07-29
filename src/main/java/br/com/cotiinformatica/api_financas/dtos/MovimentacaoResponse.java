package br.com.cotiinformatica.api_financas.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoResponse(
        UUID id,
        String nome,
        LocalDate data,
        BigDecimal valor,
        String tipo,
        CategoriaResponse categoria
) {
}
