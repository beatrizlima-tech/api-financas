package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.components.UsuarioAutenticadoComponent;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;
import br.com.cotiinformatica.api_financas.services.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final UsuarioAutenticadoComponent usuarioAutenticadoComponent;

    public MovimentacaoController(MovimentacaoService movimentacaoService, UsuarioAutenticadoComponent usuarioAutenticadoComponent) {

        this.movimentacaoService = movimentacaoService;
        this.usuarioAutenticadoComponent = usuarioAutenticadoComponent;

    }

    @PostMapping("/criar")
    public ResponseEntity<MovimentacaoResponse> criar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody MovimentacaoRequest request) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.criar(usuarioId, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<MovimentacaoResponse> alterar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @Valid @RequestBody MovimentacaoRequest request) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.alterar(usuarioId, id, request);

            return ResponseEntity.ok(response);

    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<MovimentacaoResponse> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.excluir(usuarioId, id);

            return ResponseEntity.ok(response);

    }

    @GetMapping("/consultar")
    public ResponseEntity<Page<MovimentacaoResponse>> consultar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim,
            @RequestParam(defaultValue = "0") int pageIndex,
            @RequestParam(defaultValue = "25") int pageSize
    ) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.consultar(
                    usuarioId,
                    dataInicio,
                    dataFim,
                    pageIndex,
                    pageSize
            );

            return ResponseEntity.ok(response);

    }

    @GetMapping("/obter/{id}")
    public ResponseEntity<MovimentacaoResponse> obter(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

             var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var response = movimentacaoService.obterPorId(usuarioId, id);

            return ResponseEntity.ok(response);

    }

    @PostMapping("/gerar-relatorio")
    public ResponseEntity<String> gerarRelatorio(@AuthenticationPrincipal Jwt jwt, @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {

            var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

            var email = usuarioAutenticadoComponent.obterEmail(jwt);

            var response = movimentacaoService.gerarRelatorioMovimentacoes(
                    usuarioId,
                    email,
                    dataInicio,
                    dataFim
            );

            return ResponseEntity.ok(response);

    }
}
