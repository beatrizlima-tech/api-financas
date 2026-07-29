package br.com.cotiinformatica.api_financas.handlers;

import br.com.cotiinformatica.api_financas.exceptions.ProcessamentoRelatorioException;
import br.com.cotiinformatica.api_financas.exceptions.RegistroNaoEncontradoException;
import br.com.cotiinformatica.api_financas.exceptions.ValidacaoException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException exception,HttpServletRequest request) {

        var fields = new LinkedHashMap<String, List<String>>();

        exception.getBindingResult()
                 .getFieldErrors()
                 .forEach(error ->
                         fields.computeIfAbsent(
                                    error.getField(),
                                    field -> new ArrayList<>()
                    ).add(error.getDefaultMessage())
                 );

        var problem = createProblem(
                HttpStatus.BAD_REQUEST,
                "Dados inválidos",
                "Um ou mais campos estão inválidos.",
                request
        );

        problem.setProperty("fields", fields);

        return problem;
    }

    @ExceptionHandler(ValidacaoException.class)
    public ProblemDetail handleValidacao(ValidacaoException exception, HttpServletRequest request) {

        return createProblem(
                HttpStatus.BAD_REQUEST,
                "Dados inválidos",
                exception.getMessage(),
                request
        );

    }

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    public ProblemDetail handleRegistroNaoEncontrado(RegistroNaoEncontradoException exception, HttpServletRequest request) {

        return createProblem(
                HttpStatus.NOT_FOUND,
                "Registro não encontrado",
                exception.getMessage(),
                request
        );


    }

    @ExceptionHandler(ProcessamentoRelatorioException.class)
    public ProblemDetail handleProcessamentoRelatorio(ProcessamentoRelatorioException exception, HttpServletRequest request) {

        log.error(
                "Falha ao processar o relatório.",
                exception
        );

        return createProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro ao processar relatório",
                "Não foi possível preparar os dados do relatório.",
                request
        );

    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception exception, HttpServletRequest request) {

        log.error(
                "Erro interno não tratado.",
                exception
        );

        return createProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro interno inesperado.",
                request
        );

    }

    private ProblemDetail createProblem(
            HttpStatus status,
            String title,
            String detail,
            HttpServletRequest request) {

        var problem = ProblemDetail.forStatusAndDetail(status, detail);

        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));

        return problem;


    }

}
