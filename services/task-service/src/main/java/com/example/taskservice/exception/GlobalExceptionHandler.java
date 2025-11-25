package com.example.taskservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tratador global de exceções para o Task Service.
 * 
 * Centraliza o tratamento de erros, evitando exposição de detalhes
 * sensíveis como stack traces em ambientes de produção.
 * 
 * Padrão: Centralized Exception Handling
 * 
 * Benefícios de Segurança:
 * - Previne vazamento de informações técnicas
 * - Padroniza respostas de erro
 * - Facilita logging seguro
 * - Melhora auditoria de falhas
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Trata erros de validação de entrada (@Valid).
     * 
     * Retorna detalhes dos campos inválidos sem expor estrutura interna.
     * 
     * @param ex Exceção de validação
     * @param request Requisição HTTP
     * @return Resposta 400 Bad Request com detalhes dos erros
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed for request {}: {}", 
                request.getDescription(false), errors.keySet());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Falha na validação dos dados de entrada",
                request.getDescription(false),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Trata erros de argumentos ilegais (ex: tarefa não encontrada).
     * 
     * @param ex Exceção
     * @param request Requisição HTTP
     * @return Resposta 404 Not Found
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        
        // Log sem expor mensagem completa (pode conter dados sensíveis)
        log.warn("Illegal argument in request {}: {}", 
                request.getDescription(false), ex.getClass().getSimpleName());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                "Recurso não encontrado",
                request.getDescription(false),
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Trata exceções genéricas não capturadas.
     * 
     * CRÍTICO: Não expõe detalhes técnicos em produção.
     * Stack traces são logados apenas internamente.
     * 
     * @param ex Exceção genérica
     * @param request Requisição HTTP
     * @return Resposta 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {
        
        // Log completo apenas em desenvolvimento
        log.error("Unhandled exception in request {}: {}", 
                request.getDescription(false), ex.getMessage(), ex);

        // Resposta genérica sem detalhes técnicos (segurança)
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Erro interno do servidor. Tente novamente mais tarde.",
                request.getDescription(false),
                null // Não expor detalhes em produção
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * DTO para respostas de erro padronizadas.
     * 
     * @param timestamp Data/hora do erro
     * @param status Código HTTP
     * @param error Código do erro (para i18n)
     * @param message Mensagem amigável
     * @param path Caminho da requisição
     * @param details Detalhes adicionais (opcional)
     */
    public record ErrorResponse(
            LocalDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, String> details
    ) {}
}
