package br.com.cotiinformatica.api_financas.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MovimentacaoRequest (
    @NotBlank(message = "O nome da movimentação é obrigatório.")
    @Size(
            min = 6,
            max = 150,
            message = "O nome da movimentação deve ter entre 6 e 150 caracteres."
    )
    String nome,

    @NotNull(message = "A data da movimentação é obrigatória.")
    LocalDate data,

    @NotNull(message = "O valor da movimentação é obrigatório.")
    @Positive(message = "O valor da movimentação deve ser maior do que zero.")
    @Digits(
            integer = 8,
            fraction = 2,
            message = "O valor da movimentação deve ter no máximo 8 dígitos inteiros e 2 casas decimais."
    )
    BigDecimal valor,

    @NotBlank(message = "O tipo da movimentação é obrigatório.")
    @Pattern(
            regexp = "^\\s*$|(?i)^\\s*(RECEITA|DESPESA)\\s*$",
            message = "O tipo da movimentação deve ser RECEITA ou DESPESA."
    )
    String tipo,

    @NotNull(message = "O ID da categoria é obrigatório.")
    UUID categoriaId

)
{

}
