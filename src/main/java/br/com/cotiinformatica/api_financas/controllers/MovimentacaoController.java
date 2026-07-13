package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import br.com.cotiinformatica.api_financas.services.MovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    @Autowired
    private MovimentacaoService movimentacaoService;

    @PostMapping("/criar")
    public ResponseEntity<?> criar(@RequestBody MovimentacaoRequest request) {
        try {
            var response = movimentacaoService.criar(request);

            return ResponseEntity.status(201).body(response);
        }
        catch (ValidacaoException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
        catch (RegistroNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/alterar")
    public ResponseEntity<?> alterar() {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/excluir")
    public ResponseEntity<?> excluir() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/consultar")
    public ResponseEntity<?> consultar() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obter")
    public ResponseEntity<?> obter() {
        return ResponseEntity.ok().build();
    }
}