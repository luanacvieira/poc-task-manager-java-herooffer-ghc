package com.example.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador responsável pelo endpoint de health check customizado.
 * 
 * Este controlador fornece um endpoint simples para verificação de disponibilidade
 * do API Gateway, retornando um status OK em formato JSON.
 * 
 * @author Task Manager Team
 * @version 1.0.0
 * @since 2025-11-24
 */
@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {

    /**
     * Endpoint GET para verificação de saúde do serviço.
     * 
     * Este método responde com um JSON contendo o status "ok", indicando
     * que o API Gateway está operacional e pronto para receber requisições.
     * 
     * Exemplo de requisição:
     * <pre>
     * GET http://localhost:8080/healthcheck
     * </pre>
     * 
     * Exemplo de resposta:
     * <pre>
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     * 
     * {
     *   "status": "ok"
     * }
     * </pre>
     * 
     * @return ResponseEntity contendo um Map com a chave "status" e valor "ok",
     *         juntamente com o código HTTP 200 (OK)
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
        // Cria um Map para estruturar a resposta JSON
        Map<String, String> response = new HashMap<>();
        
        // Define a chave "status" com o valor "ok"
        response.put("status", "ok");
        
        // Retorna a resposta com status HTTP 200 (OK) e o corpo JSON
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
