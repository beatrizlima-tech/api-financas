package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;
import br.com.cotiinformatica.api_financas.dtos.RelatorioMovimentacaoRequest;
import br.com.cotiinformatica.api_financas.entities.Movimentacao;
import br.com.cotiinformatica.api_financas.enums.TipoMovimentacao;
import br.com.cotiinformatica.api_financas.exceptions.ProcessamentoRelatorioException;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import br.com.cotiinformatica.api_financas.repositories.MovimentacaoRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class MovimentacaoService {

    private final CategoriaRepository categoriaRepository;

    private final MovimentacaoRepository movimentacaoRepository;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    private final Queue queue;

    public MovimentacaoService(
            CategoriaRepository categoriaRepository,
            MovimentacaoRepository movimentacaoRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            @Qualifier("relatoriosQueue") Queue queue) {

        this.categoriaRepository = categoriaRepository;

        this.movimentacaoRepository = movimentacaoRepository;

        this.rabbitTemplate = rabbitTemplate;

        this.objectMapper = objectMapper;

        this.queue = queue;

    }

        // Método para criar uma movimentação no banco de dados

    @Transactional
    public MovimentacaoResponse criar(UUID usuarioId, MovimentacaoRequest request) {

        //Verificar se a categoria existe no banco de dados
        var categoria = categoriaRepository
                .findByIdAndUsuarioId(
                        request.categoriaId(),
                        usuarioId
                )
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                                "Categoria não encontrada."
                        )
                );

        //Criando um objeto da classe Movimentação
        var movimentacao = new Movimentacao();

        //Preencher os dados da movimentação
        movimentacao.setUsuarioId(usuarioId);
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(request.valor());
        movimentacao.setTipo(TipoMovimentacao.valueOf(request.tipo()));

        movimentacao.setCategoria(categoria);

        //Salvar a movimentação no banco de dados
        movimentacaoRepository.save(movimentacao);

        //Retornar os dados da movimentação cadastrada
        return toResponse(movimentacao);
    }

    @Transactional
    public MovimentacaoResponse alterar(UUID usuarioId, UUID id, MovimentacaoRequest request) {

        //Procura a movimentação e confirma que pertence ao usuário
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                                "Movimentação não encontrada."
                        )
                );

        //Procura a categoria escolhida e confirma que pertence ao usuário
        var categoria = categoriaRepository
                .findByIdAndUsuarioId(request.categoriaId(), usuarioId)
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                                "Categoria não encontrada."
                        )
                );

        //Preencher os dados da movimentação
        movimentacao.setNome(request.nome());
        movimentacao.setData(request.data());
        movimentacao.setValor(request.valor());
        movimentacao.setTipo(TipoMovimentacao.valueOf(request.tipo()));
        movimentacao.setCategoria(categoria);

        //Salvar a movimentação no banco de dados
        movimentacaoRepository.save(movimentacao);

        //Retornar os dados da movimentação alterada usando o DTO
        return toResponse(movimentacao);
    }

    @Transactional
    public MovimentacaoResponse excluir(UUID usuarioId, UUID id) {

        //Consultar a movimentação no banco de dados pelo ID
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                                "Movimentação não encontrada."
                        )
                );

        //Excluir a movimentação no banco de dados
        movimentacaoRepository.delete(movimentacao);

        //Retornar os dados da movimentação excluída
        return toResponse(movimentacao);
    }

    /*
        Método para consultar as movimentações por periodo de datas e com paginação
     */
    public Page<MovimentacaoResponse> consultar(
            UUID usuarioId,
            LocalDate dataInicio,
            LocalDate dataFim,
            int pageIndex,
            int pageSize
    ) {

        //Validar as datas

        if (dataInicio == null || dataFim == null) {
            throw new ValidacaoException(
                    "As datas de início e fim são obrigatórias."
            );
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new ValidacaoException(
                    "A data de início não pode ser maior do que a data de fim."
            );
        }

        //Validar a paginação
        if (pageIndex < 0) {
            throw new ValidacaoException(
                    "O índice da página não pode ser negativo."
            );
        }

        if (pageSize <= 0) {
            throw new ValidacaoException(
                    "O tamanho da página deve ser maior que zero."
            );
        }

        //Configurando a paginação
        if (pageSize > 25) pageSize = 25;

        var pageable = PageRequest.of(pageIndex, pageSize);

        //Consultar as movimentações no banco de dados
        var movimentacoes = movimentacaoRepository.findByUsuarioIdAndData(
                usuarioId,
                dataInicio,
                dataFim,
                pageable
        );

        //Retornar os dados usando o DTO
        return movimentacoes.map(this::toResponse);
    }

    /*
        Método para consultar uma movimentação pelo ID
     */
    public MovimentacaoResponse obterPorId(UUID usuarioId, UUID id) {

        //Consultar a movimentação pelo ID no banco de dados
        var movimentacao = movimentacaoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                                "Movimentação não encontrada."
                        )
                );

        //Retornando os dados da movimentação
        return toResponse(movimentacao);
    }

    // Método para gerar o relatório das movimentações

    public String gerarRelatorioMovimentacoes(UUID usuarioId, String email, LocalDate dataInicio, LocalDate dataFim) {

        if (dataInicio == null || dataFim == null) {
            throw new ValidacaoException(
                    "As datas de início e fim são obrigatórias."
            );
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new ValidacaoException(
                    "A data de início não pode ser maior do que a data de fim."
            );
        }

        var movimentacoes = movimentacaoRepository.findByUsuarioIdAndData(usuarioId, dataInicio, dataFim, Pageable.unpaged());

        if(movimentacoes.isEmpty()) {
            return "Nenhuma movimentação foi encontrada para o período de datas informado.";
        }

        var response = movimentacoes.stream()
                .map(this::toResponse)
                .toList();

        try{
            var movimentacoesJson = objectMapper.writeValueAsString(response);

            var relatorioMovimentacao  = new RelatorioMovimentacaoRequest(email, dataInicio, dataFim, movimentacoesJson);

            var relatorioJson = objectMapper.writeValueAsString(relatorioMovimentacao);

            rabbitTemplate.convertAndSend(queue.getName(), relatorioJson);

        }
        catch (JacksonException exception) {

            throw new ProcessamentoRelatorioException(
                    "Não foi possível preparar os dados do relatório.",
                    exception
            );
        }

        return "Sucesso! Os dados foram enviados para a fila de processamento do relatório.";
    }

    // Método para retornar os dados no DTO de resposta

    private MovimentacaoResponse toResponse(Movimentacao movimentacao) {
        return new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getNome(),
                movimentacao.getData(),
                movimentacao.getValor(),
                movimentacao.getTipo().toString(),
                new CategoriaResponse(
                        movimentacao.getCategoria().getId(),
                        movimentacao.getCategoria().getNome()
                )
        );
    }
}