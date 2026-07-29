package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.components.UsuarioAutenticadoComponent;
import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("criar")
    public ResponseEntity<?> criar(@AuthenticationPrincipal Jwt jwt, @RequestBody CategoriaRequest request) {

        try {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.criar(usuarioId, request);

            //HTTP 201 (CREATED)
            return ResponseEntity.status(201).body(response);

        } catch (ValidacaoException e) {

            //HTTP 400 (BAD REQUEST)
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("alterar/{id}")
    public ResponseEntity<?> alterar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody CategoriaRequest request) {

        try {
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.alterar(usuarioId, id, request);

            return ResponseEntity.ok(response);
        }
        catch (RegistroNaoEncontradoException e) {

            return ResponseEntity.status(404).body(e.getMessage());

        }
        catch (ValidacaoException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("excluir/{id}")
    public ResponseEntity<?> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        try{
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.excluir(usuarioId, id);

            return ResponseEntity.ok(response);
        }
        catch (RegistroNaoEncontradoException e) {

            return ResponseEntity.status(404).body(e.getMessage());
        }

    }

    @GetMapping("consultar")
    public ResponseEntity<?> consultar(@AuthenticationPrincipal Jwt jwt) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.consultar(usuarioId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("obter/{id}")
    public ResponseEntity<?> obterPorId(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        try{

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = categoriaService.obterPorId(usuarioId, id);

            return ResponseEntity.ok(response);
        }
        catch (RegistroNaoEncontradoException e) {

            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}