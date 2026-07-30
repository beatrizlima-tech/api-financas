package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.components.UsuarioAutenticadoComponent;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoRequest;
import br.com.cotiinformatica.api_financas.dtos.MovimentacaoResponse;
import br.com.cotiinformatica.api_financas.services.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;


@Tag(
        name = "Movimentações",
        description = "Criação, alteração, exclusão, consulta e geração de relatórios das movimentações do usuário autenticado."
)
@RestController
@RequestMapping("/api/v1/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final UsuarioAutenticadoComponent usuarioAutenticadoComponent;

    public MovimentacaoController(MovimentacaoService movimentacaoService, UsuarioAutenticadoComponent usuarioAutenticadoComponent) {

        this.movimentacaoService = movimentacaoService;
        this.usuarioAutenticadoComponent = usuarioAutenticadoComponent;

    }

    @Operation(
            summary = "Cadastrar movimentação",
            description = "Cadastra uma receita ou despesa para o usuário autenticado, vinculada a uma categoria pertencente ao mesmo usuário."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Movimentação cadastrada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = MovimentacaoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da movimentação inválidos.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria não encontrada para o usuário autenticado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @PostMapping("/criar")
    public ResponseEntity<MovimentacaoResponse> criar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody MovimentacaoRequest request) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = movimentacaoService.criar(usuarioId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(
            summary = "Alterar movimentação",
            description = "Altera uma movimentação pertencente ao usuário autenticado e valida a categoria informada."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimentação alterada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = MovimentacaoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da movimentação inválidos.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimentação ou categoria não encontrada para o usuário autenticado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @PutMapping("/alterar/{id}")
    public ResponseEntity<MovimentacaoResponse> alterar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @Valid @RequestBody MovimentacaoRequest request) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = movimentacaoService.alterar(usuarioId, id, request);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Excluir movimentação",
            description = "Exclui uma movimentação pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimentação excluída com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = MovimentacaoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "UUID inválido.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimentação não encontrada para o usuário autenticado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<MovimentacaoResponse> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = movimentacaoService.excluir(usuarioId, id);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Consultar movimentações",
            description = "Consulta as movimentações do usuário autenticado por período, com paginação."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Página de movimentações consultada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Page.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Período ou parâmetros de paginação inválidos.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping("/consultar")
    public ResponseEntity<Page<MovimentacaoResponse>> consultar(
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(
                    description = "Data inicial do período.",
                    example = "2026-07-01",
                    required = true
            )
            @RequestParam LocalDate dataInicio,

            @Parameter(
                    description = "Data final do período.",
                    example = "2026-07-31",
                    required = true
            )
            @RequestParam LocalDate dataFim,

            @Parameter(
                    description = "Índice da página, começando em zero.",
                    example = "0"
            )
            @RequestParam(defaultValue = "0") int pageIndex,

            @Parameter(
                    description = "Quantidade de registros por página, limitada a 25.",
                    example = "25"
            )
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

    @Operation(
            summary = "Obter movimentação por ID",
            description = "Obtém uma movimentação pelo UUID, desde que pertença ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Movimentação obtida com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = MovimentacaoResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "UUID inválido.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Movimentação não encontrada para o usuário autenticado.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @GetMapping("/obter/{id}")
    public ResponseEntity<MovimentacaoResponse> obter(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = movimentacaoService.obterPorId(usuarioId, id);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Solicitar relatório de movimentações",
            description = "Consulta as movimentações do período e, quando houver dados, envia uma mensagem ao RabbitMQ para processamento assíncrono do relatório."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação de relatório processada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(
                                    implementation = String.class,
                                    example = "Sucesso! Os dados foram enviados para a fila de processamento do relatório."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Período informado inválido.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente, inválido ou expirado.",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao preparar ou enviar os dados do relatório.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProblemDetail.class
                            )
                    )
            )
    })
    @PostMapping("/gerar-relatorio")
    public ResponseEntity<String> gerarRelatorio(@AuthenticationPrincipal Jwt jwt,

                                                 @Parameter(
                                                         description = "Data inicial do período do relatório.",
                                                         example = "2026-07-01",
                                                         required = true
                                                 )
                                                 @RequestParam LocalDate dataInicio,

                                                 @Parameter(
                                                         description = "Data final do período do relatório.",
                                                         example = "2026-07-31",
                                                         required = true
                                                 )
                                                 @RequestParam LocalDate dataFim
    ) {

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
