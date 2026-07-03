package br.com.cotiinformatica.api_financas.repositories;

import br.com.cotiinformatica.api_financas.entities.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {
}
