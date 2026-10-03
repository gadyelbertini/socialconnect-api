package br.com.socialconnect.api.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Erro de validacao (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        boolean estoqueInvalido = ex.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> "EstoqueNaoNegativo".equals(error.getCode()));
        HttpStatus status = estoqueInvalido
                ? HttpStatus.UNPROCESSABLE_ENTITY
                : HttpStatus.BAD_REQUEST;

        List<ProblemDetail.FieldError> errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(e -> new ProblemDetail.FieldError(
                        e.getField(),
                        e.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ProblemDetail problem = new ProblemDetail(
                estoqueInvalido
                        ? "https://socialconnect.api/errors/estoque-invalido"
                        : "https://socialconnect.api/errors/validacao",
                estoqueInvalido ? "Estoque inválido" : "Erro de validação",
                status.value(),
                "Um ou mais campos são inválidos.",
                request.getRequestURI(),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(status).body(problem);
    }

    @ExceptionHandler(ProdutoNomeDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleProdutoNomeDuplicado(
            ProdutoNomeDuplicadoException ex, HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/produto-duplicado",
                "Produto já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(EstoqueNegativoException.class)
    public ResponseEntity<ProblemDetail> handleEstoqueNegativo(
            EstoqueNegativoException ex, HttpServletRequest request) {
        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/estoque-invalido",
                "Estoque inválido",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    // 2. CPF duplicado (409)
    @ExceptionHandler(CpfDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleCpfDuplicado(
            CpfDuplicadoException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/cpf-duplicado",
                "CPF já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // 3. Recurso customizado nao encontrado (404)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(
            RecursoNaoEncontradoException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nao-encontrado",
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // 4. Entidade JPA nao encontrada (404)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(
            EntityNotFoundException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nao-encontrado",
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage() != null ? ex.getMessage() : "Registro não encontrado no banco de dados.",
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // 5. NoSuchElementException / Optional vazio (404)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElement(
            NoSuchElementException ex, HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nao-encontrado",
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                "O registro solicitado não foi encontrado.",
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // 6. Rota ou Endpoint não encontrado (Erro 404 nativo do Spring)
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex,
            HttpServletRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "about:blank",
                "Endpoint não encontrado",
                HttpStatus.NOT_FOUND.value(),
                "O caminho requisitado não existe na API.",
                request.getRequestURI(),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
}