package br.com.cotiinformatica.api_financas.repositories;

import br.com.cotiinformatica.api_financas.entities.Movimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {

    Optional<Movimentacao> findByIdAndUsuarioId(UUID id, UUID usuarioId);

    @Query("""
        SELECT m
        FROM Movimentacao m
        WHERE m.usuarioId = :usuarioId
             AND m.data BETWEEN :pDataInicio AND :pDataFim
                 ORDER BY m.data DESC
    """)
    Page<Movimentacao> findByUsuarioIdAndData(
            @Param("usuarioId") UUID usuarioId,
            @Param("pDataInicio") LocalDate dataInicio,
            @Param("pDataFim") LocalDate dataFim,
            Pageable paginacao
    );

    @Query("""
        SELECT m
        FROM Movimentacao m
        WHERE m.usuarioId = :usuarioId
             AND m.data BETWEEN :pDataInicio AND :pDataFim
                 ORDER BY m.data DESC
    """)
    List<Movimentacao> findByUsuarioIdAndData(
            @Param("usuarioId") UUID usuarioId,
            @Param("pDataInicio") LocalDate dataInicio,
            @Param("pDataFim") LocalDate dataFim
    );

}