package br.com.cotiinformatica.api_financas.services;

import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.entities.Categoria;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {

        this.categoriaRepository = categoriaRepository;
    }

    public CategoriaResponse criar(UUID usuarioId, CategoriaRequest request) {

        //Validar se os dados da categoria foram enviados
        if (request == null) {
            throw new ValidacaoException(
                    "Os dados da categoria são obrigatórios."
            );
        }

        //Criando um objeto da entidade 'Categoria'
        var categoria = new Categoria();

        categoria.setUsuarioId(usuarioId);

        //Capturando os dados recebidos
        categoria.setNome(request.nome());

        //Executar a validação
        validarCategoria(categoria);

        //Remover espaços extras antes de salvar
        categoria.setNome(categoria.getNome().trim());

        //Salvar a categoria no banco de dados
        categoriaRepository.save(categoria);

        //Retornar a resposta
        return toResponse(categoria);
    }

    public CategoriaResponse alterar(UUID usuarioId, UUID id, CategoriaRequest request) {

        //Buscar a categoria no banco de dados através do ID
        var categoria = buscarCategoriaDoUsuario(id, usuarioId);

        //Validar se os dados da categoria foram envidados
        if (request == null) {
            throw new ValidacaoException(
                    "Os dados da categoria são obrigatórios."
            );
        }

        //Capturar o nome da categoria que será alterado
        categoria.setNome(request.nome());

        //Validar o nome da categoria
        validarCategoria(categoria);

        //Remover espaços extras antes de salvar
        categoria.setNome(categoria.getNome().trim());

        //Atualizar no banco de dados
        categoriaRepository.save(categoria);

        //Retornar a resposta
        return toResponse(categoria);
    }

    public CategoriaResponse excluir(UUID usuarioId, UUID id) {

        //Buscar a categoria no banco de dados através do ID
        var categoria = buscarCategoriaDoUsuario(id, usuarioId);

        //Excluindo no banco de dados
        categoriaRepository.delete(categoria);

        //Retornar a resposta
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

    private void validarCategoria(Categoria categoria) {

        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new ValidacaoException(
                    "O nome da categoria é obrigatório."
            );
        }
        if (categoria.getNome().trim().length() < 6) {
            throw new ValidacaoException(
                    "O nome da categoria deve ter pelo menos 6 caracteres."
            );
        }
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
}
