package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.entities.Categoria;
import br.com.cotiinformatica.api_financas.exceptions.CategoriaEmUsoException;
import br.com.cotiinformatica.api_financas.exceptions.CategoriaJaCadastradaException;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import br.com.cotiinformatica.api_financas.repositories.MovimentacaoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final MovimentacaoRepository movimentacaoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, MovimentacaoRepository movimentacaoRepository) {

        this.categoriaRepository = categoriaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public CategoriaResponse criar(UUID usuarioId, CategoriaRequest request) {

        validarNomeDisponivel(
                usuarioId,
                request.nome(),
                null
        );

        var categoria = new Categoria();

        categoria.setUsuarioId(usuarioId);
        categoria.setNome(request.nome());

        salvarCategoria(categoria);

        return toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse alterar(UUID usuarioId, UUID id, CategoriaRequest request) {

        var categoria = buscarCategoriaDoUsuario(id,usuarioId);

        validarNomeDisponivel(
                usuarioId,
                request.nome(),
                id
        );

        categoria.setNome(request.nome());

        salvarCategoria(categoria);

        return toResponse(categoria);
    }

    @Transactional
    public CategoriaResponse excluir(UUID usuarioId, UUID id) {

        var categoria = buscarCategoriaDoUsuario(id, usuarioId);

        if(movimentacaoRepository.existsByCategoriaIdAndUsuarioId(id, usuarioId)) {
            throw new CategoriaEmUsoException(
                    "A categoria não pode ser excluída porque possui movimentações vinculadas."
            );
        }

        categoriaRepository.delete(categoria);

        return toResponse(categoria);
    }

    public List<CategoriaResponse> consultar(UUID usuarioId) {

        var categorias = categoriaRepository.findAllByUsuarioIdOrderByNomeAsc(usuarioId);

        return categorias.stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaResponse obterPorId(UUID usuarioId, UUID id) {

        var categoria = buscarCategoriaDoUsuario(id, usuarioId);

        return toResponse(categoria);

    }

    private CategoriaResponse toResponse(Categoria categoria) {
        //Retornar a resposta
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome()
        );
    }

    private Categoria buscarCategoriaDoUsuario(UUID id, UUID usuarioId) {

        return categoriaRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                             "Categoria não encontrada."
                        )
                );

    }

    private void validarNomeDisponivel(
            UUID usuarioId,
            String nome,
            UUID categoriaId) {

        final boolean nomeJaUtilizado;

        if (categoriaId == null) {

            nomeJaUtilizado = categoriaRepository
                    .existsByUsuarioIdAndNomeIgnoreCase(
                            usuarioId,
                            nome
                    );

        } else {

            nomeJaUtilizado = categoriaRepository
                    .existsByUsuarioIdAndNomeIgnoreCaseAndIdNot(
                            usuarioId,
                            nome,
                            categoriaId
                    );
        }

        if (nomeJaUtilizado) {
            throw new CategoriaJaCadastradaException();
        }
    }

    private void salvarCategoria(Categoria categoria) {

        try {
            categoriaRepository.saveAndFlush(categoria);
        }
        catch (DataIntegrityViolationException exception) {
            throw new CategoriaJaCadastradaException();
        }
    }

}
