package br.com.cotiinformatica.api_financas.controllers;

import br.com.cotiinformatica.api_financas.components.UsuarioAutenticadoComponent;
import br.com.cotiinformatica.api_financas.dtos.CategoriaRequest;
import br.com.cotiinformatica.api_financas.dtos.CategoriaResponse;
import br.com.cotiinformatica.api_financas.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Categorias",
        description = "Criação, alteração, exclusão e consulta das categorias do usuário autenticado."
)
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final UsuarioAutenticadoComponent usuarioAutenticadoComponent;

    public CategoriaController(CategoriaService categoriaService, UsuarioAutenticadoComponent usuarioAutenticadoComponent) {

        this.categoriaService = categoriaService;
        this.usuarioAutenticadoComponent = usuarioAutenticadoComponent;

    }

    @Operation(
            summary = "Cadastrar categoria",
            description = "Cadastra uma nova categoria financeira para o usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria cadastrada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = CategoriaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da categoria inválidos.",
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
    @PostMapping("/criar")
    public ResponseEntity<CategoriaResponse> criar(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CategoriaRequest request) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.criar(usuarioId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(
            summary = "Alterar categoria",
            description = "Altera uma categoria pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria alterada com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = CategoriaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da categoria ou UUID inválidos.", content = @Content(
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
    @PutMapping("/alterar/{id}")
    public ResponseEntity<CategoriaResponse> alterar(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @Valid @RequestBody CategoriaRequest request) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.alterar(usuarioId, id, request);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Excluir categoria",
            description = "Exclui uma categoria pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria excluída com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = CategoriaResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "UUID da categoria inválido.",
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
    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<CategoriaResponse> excluir(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.excluir(usuarioId, id);

        return ResponseEntity.ok(response);

    }

    @Operation(
            summary = "Consultar categorias",
            description = "Consulta, em ordem alfabética, as categorias pertencentes ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorias consultadas com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = CategoriaResponse.class
                                    )
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
    public ResponseEntity<List<CategoriaResponse>> consultar(@AuthenticationPrincipal Jwt jwt) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.consultar(usuarioId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Obter categoria por ID",
            description = "Obtém uma categoria pelo UUID, desde que pertença ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoria obtida com sucesso.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = CategoriaResponse.class
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
    @GetMapping("/obter/{id}")
    public ResponseEntity<CategoriaResponse> obterPorId(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        var usuarioId = usuarioAutenticadoComponent.obterUsuarioId(jwt);

        var response = categoriaService.obterPorId(usuarioId, id);

        return ResponseEntity.ok(response);

    }

}
