package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.components.UsuarioAutenticadoComponent;
import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UsuarioAutenticadoComponent usuarioAutenticadoComponent;

    public CategoriaController(CategoriaService categoriaService, UsuarioAutenticadoComponent usuarioAutenticadoComponent) {

        this.categoriaService = categoriaService;
        this.usuarioAutenticadoComponent = usuarioAutenticadoComponent;

    }

    @PostMapping("/criar")
    public ResponseEntity<CategoriaResponse> criar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CategoriaRequest request) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.criar(usuarioId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<CategoriaResponse> alterar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @Valid @RequestBody CategoriaRequest request) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.alterar(usuarioId, id, request);

            return ResponseEntity.ok(response);

    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<CategoriaResponse> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.excluir(usuarioId, id);

            return ResponseEntity.ok(response);

    }

    @GetMapping("/consultar")
    public ResponseEntity<List<CategoriaResponse>> consultar(@AuthenticationPrincipal Jwt jwt) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.consultar(usuarioId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/obter/{id}")
    public ResponseEntity<CategoriaResponse> obterPorId(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.obterPorId(usuarioId, id);

            return ResponseEntity.ok(response);

    }

}