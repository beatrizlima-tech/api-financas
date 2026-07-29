package br.com.cotiinformatica.api_financas.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categorias")
@Getter
@Setter
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "nome", length = 50, nullable = false)
    private String nome;

    @Column(name= "usuario_id", nullable = false, updatable = false)
    private UUID usuarioId;

    @OneToMany(mappedBy = "categoria")
    private List<Movimentacao> movimentacoes;
}

