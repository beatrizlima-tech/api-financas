package br.com.cotiinformatica.api_financas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "O nome da categoria é obrigatório.")
        @Size(
                min = 2,
                max = 50,
                message = "O nome da categoria deve ter entre 2 e 50 caracteres."
        )
        String nome
) {

        public CategoriaRequest {
                if(nome != null) {
                        nome = nome.trim();
                }
        }

}
