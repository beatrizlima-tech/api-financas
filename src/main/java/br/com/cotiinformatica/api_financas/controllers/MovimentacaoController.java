package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.components.UsuarioAutenticadoComponent;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.services.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @Autowired
    private UsuarioAutenticadoComponent usuarioAutenticadoComponent;

    @PostMapping("criar")
    public ResponseEntity<?> criar(@AuthenticationPrincipal Jwt jwt, @RequestBody MovimentacaoRequest request) {
        try {
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.criar(usuarioId, request);

            return ResponseEntity.status(201).body(response);
        }
        catch (ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        catch(RegistroNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("alterar/{id}")
    public ResponseEntity<?> alterar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody MovimentacaoRequest request) {
        try {
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.alterar(usuarioId, id, request);

            return ResponseEntity.status(200).body(response);
        }
        catch (ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        catch(RegistroNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("excluir/{id}")
    public ResponseEntity<?> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        try {
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.excluir(usuarioId, id);

            return ResponseEntity.status(200).body(response);
        }
        catch(RegistroNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("consultar")
    public ResponseEntity<?> consultar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "25") int pageSize
    ) {
        try {
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.consultar(
                    usuarioId,
                    dataInicio,
                    dataFim,
                    pageIndex,
                    pageSize
            );

            return ResponseEntity.status(200).body(response);
        }
        catch(ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("obter/{id}")
    public ResponseEntity<?> obter(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
        try {
             var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.obterPorId(usuarioId, id);

            return ResponseEntity.status(200).body(response);
        }
        catch(RegistroNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("gerar-relatorio")
    public ResponseEntity<?> gerarRelatorio(@AuthenticationPrincipal Jwt jwt, @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) throws Exception{
        try {
            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var email = usuarioAutenticadoComponent.obterEmail(jwt);

            var response = movimentacaoService.gerarRelatorioMovimentacoes(
                    usuarioId,
                    email,
                    dataInicio,
                    dataFim
            );

            return ResponseEntity.status(200).body(response);
        }
        catch(ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
