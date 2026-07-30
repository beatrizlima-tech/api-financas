package br.com.cotiinformatica.api_financas;

import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.entities.Categoria;
import br.com.cotiinformatica.api_financas.exceptions.CategoriaEmUsoException;
import br.com.cotiinformatica.api_financas.exceptions.CategoriaJaCadastradaException;
import br.com.cotiinformatica.api_financas.repositories.CategoriaRepository;
import br.com.cotiinformatica.api_financas.repositories.MovimentacaoRepository;
import br.com.cotiinformatica.api_financas.services.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    private CategoriaService categoriaService;

    private UUID usuarioId;
    private UUID categoriaId;
    private Categoria categoria;

    @BeforeEach
    void setUp() {

        categoriaService = new CategoriaService(
                categoriaRepository,
                movimentacaoRepository
        );

        usuarioId = UUID.randomUUID();
        categoriaId = UUID.randomUUID();

        categoria = new Categoria();
        categoria.setId(categoriaId);
        categoria.setUsuarioId(usuarioId);
        categoria.setNome("Alimentação");
    }

    @Test
    @DisplayName("Deve impedir a exclusão de uma categoria que possui movimentações")
    void deveImpedirExclusaoDeCategoriaEmUso() {

        when(categoriaRepository.findByIdAndUsuarioId(
                categoriaId,
                usuarioId
        )).thenReturn(Optional.of(categoria));

        when(movimentacaoRepository
                .existsByCategoriaIdAndUsuarioId(
                        categoriaId,
                        usuarioId
                ))
                .thenReturn(true);

        var exception = assertThrows(
                CategoriaEmUsoException.class,
                () -> categoriaService.excluir(
                        usuarioId,
                        categoriaId
                )
        );

        assertEquals(
                "A categoria não pode ser excluída porque possui movimentações vinculadas.",
                exception.getMessage()
        );

        verify(categoriaRepository, never())
                .delete(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve excluir uma categoria que não possui movimentações")
    void deveExcluirCategoriaSemMovimentacoes() {

        when(categoriaRepository.findByIdAndUsuarioId(
                categoriaId,
                usuarioId
        )).thenReturn(Optional.of(categoria));

        when(movimentacaoRepository
                .existsByCategoriaIdAndUsuarioId(
                        categoriaId,
                        usuarioId
                ))
                .thenReturn(false);

        var response = categoriaService.excluir(
                usuarioId,
                categoriaId
        );

        assertEquals(categoriaId, response.id());
        assertEquals("Alimentação", response.nome());

        verify(categoriaRepository).delete(categoria);
    }

    @Test
    @DisplayName("Deve traduzir conflito do banco ao criar categoria duplicada")
    void deveTraduzirConflitoDoBancoAoCriarCategoriaDuplicada() {

        var request = new CategoriaRequest("Alimentação");

        when(categoriaRepository
                .existsByUsuarioIdAndNomeIgnoreCase(
                        usuarioId,
                        request.nome()
                ))
                .thenReturn(false);

        when(categoriaRepository
                .saveAndFlush(any(Categoria.class)))
                .thenThrow(
                        new DataIntegrityViolationException(
                                "Violação do índice único"
                        )
                );

        assertThrows(
                CategoriaJaCadastradaException.class,
                () -> categoriaService.criar(
                        usuarioId,
                        request
                )
        );

        verify(categoriaRepository)
                .saveAndFlush(any(Categoria.class));
    }
}