package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;
import br.com.cotiinformatica.api_financas.entities.Movimentacao;
import br.com.cotiinformatica.api_financas.enums.TipoMovimentacao;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import br.com.cotiinformatica.api_financas.repositories.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MovimentacaoService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    /*
        Método para criar uma movimentação no banco de dados
     */
    public MovimentacaoResponse criar(MovimentacaoRequest request) {

        // Executar as validações antes de acessar o banco de dados
        validarMovimentacao(request);

        // Verificar se a categoria existe no banco de dados
        var categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException("Categoria não encontrada.")
                );

        // Criar um objeto da classe Movimentacao
        var movimentacao = new Movimentacao();

        // Preencher os dados da movimentação
        movimentacao.setNome(request.nome().trim());
        movimentacao.setData(request.data());
        movimentacao.setValor(BigDecimal.valueOf(request.valor()));
        movimentacao.setTipo(
                TipoMovimentacao.valueOf(
                        request.tipo().trim().toUpperCase()
                )
        );
        movimentacao.setCategoria(categoria);

        // Salvar a movimentação no banco de dados
        var movimentacaoSalva = movimentacaoRepository.save(movimentacao);

        // Retornar os dados da movimentação cadastrada
        return toResponse(movimentacaoSalva);
    }

    /*
        Método para validar os dados da movimentação
     */
    public void validarMovimentacao(MovimentacaoRequest request) {

        if (request == null) {
            throw new ValidacaoException(
                    "Os dados da movimentação são obrigatórios."
            );
        }

        if (request.nome() == null || request.nome().trim().isEmpty()) {
            throw new ValidacaoException(
                    "O nome da movimentação é obrigatório."
            );
        }

        if (request.nome().trim().length() < 6) {
            throw new ValidacaoException(
                    "O nome da movimentação deve ter pelo menos 6 caracteres."
            );
        }

        if (request.data() == null) {
            throw new ValidacaoException(
                    "A data da movimentação é obrigatória."
            );
        }

        if (request.valor() == null) {
            throw new ValidacaoException(
                    "O valor da movimentação é obrigatório."
            );
        }

        if (request.valor() <= 0) {
            throw new ValidacaoException(
                    "O valor da movimentação deve ser maior que zero."
            );
        }

        if (request.tipo() == null || request.tipo().trim().isEmpty()) {
            throw new ValidacaoException(
                    "O tipo da movimentação é obrigatório."
            );
        }

        var tipo = request.tipo().trim().toUpperCase();

        if (!tipo.equals("DESPESA") && !tipo.equals("RECEITA")) {
            throw new ValidacaoException(
                    "O tipo da movimentação deve ser RECEITA ou DESPESA."
            );
        }

        if (request.categoriaId() == null) {
            throw new ValidacaoException(
                    "A categoria da movimentação é obrigatória."
            );
        }
    }

    /*
        Método para retornar os dados do DTO de resposta da movimentação
     */
    public MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getNome(),
                movimentacao.getData(),
                movimentacao.getValor().doubleValue(),
                movimentacao.getTipo().toString(),
                new CategoriaResponse(
                        movimentacao.getCategoria().getId(),
                        movimentacao.getCategoria().getNome()
                )
        );
    }
}