package br.com.cotiinformatica.api_financas.repositories;

import br.com.cotiinformatica.api_financas.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository
        extends JpaRepository<Categoria, UUID> {

    Optional<Categoria> findByIdAndUsuarioId(
            UUID id,
            UUID usuarioId
    );

    List<Categoria> findAllByUsuarioIdOrderByNomeAsc(
            UUID usuarioId
    );
}