package br.com.cotiinformatica.api_financas.dtos;

import java.util.UUID;

public record CategoriaResponse(
        UUID id, //id da categoria
        String nome //nome da categoria
) {
}
