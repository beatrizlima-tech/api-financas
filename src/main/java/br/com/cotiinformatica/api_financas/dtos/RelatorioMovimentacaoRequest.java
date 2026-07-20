package br.com.cotiinformatica.api_financas.dtos;

import java.time.LocalDate;

public record RelatorioMovimentacaoRequest(
        String usuario,
        LocalDate dataInicio,
        LocalDate dataFim,
        String dadosAnalise
) {
}