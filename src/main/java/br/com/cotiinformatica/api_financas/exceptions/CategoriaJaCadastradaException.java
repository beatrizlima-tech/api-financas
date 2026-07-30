package br.com.cotiinformatica.api_financas.exceptions;

public class CategoriaJaCadastradaException extends RuntimeException {

    public CategoriaJaCadastradaException() {
        super(
                "Já existe uma categoria com esse nome para este usuário."
        );
    }
}