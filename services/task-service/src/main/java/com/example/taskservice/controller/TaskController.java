package com.example.taskservice.controller;

import com.example.taskservice.domain.Task;
import com.example.taskservice.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gerenciamento de tarefas.
 * 
 * Expõe endpoints HTTP para operações CRUD de tarefas.
 * Segue princípios RESTful com status HTTP apropriados.
 * 
 * Base URL: /api/tasks
 * 
 * Endpoints disponíveis:
 * - GET    /api/tasks          - Lista todas as tarefas
 * - GET    /api/tasks/{id}     - Busca tarefa por ID
 * - POST   /api/tasks          - Cria nova tarefa
 * - PUT    /api/tasks/{id}     - Atualiza tarefa existente
 * - DELETE /api/tasks/{id}     - Exclui tarefa
 * - GET    /api/tasks/user/{userId} - Lista tarefas de um usuário
 * 
 * @author Task Manager Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/tasks")
// CORS configurado via SecurityConfig para restringir origens permitidas
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService service;

    /**
     * Lista todas as tarefas do sistema.
     * 
     * @return Lista de tarefas (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<Task>> listAll() {
        log.debug("GET /api/tasks - Listando todas as tarefas");
        List<Task> tasks = service.findAll();
        log.debug("Retornando {} tarefas", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Busca uma tarefa específica por ID.
     * 
     * @param id ID da tarefa
     * @return Tarefa encontrada (200 OK) ou 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable Long id) {
        log.debug("GET /api/tasks/{} - Buscando tarefa", id);
        return service.findById(id)
                .map(task -> {
                    log.debug("Tarefa {} encontrada", id);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> {
                    log.warn("Tentativa de acesso a tarefa inexistente: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Busca tarefas de um usuário específico.
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas do usuário (200 OK)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Task>> getByUserId(
            @PathVariable 
            @Pattern(regexp = "^[a-zA-Z0-9_-]{3,50}$", message = "User ID inválido") 
            String userId) {
        log.debug("GET /api/tasks/user - Buscando tarefas");
        List<Task> tasks = service.findByUserId(userId);
        log.debug("Encontradas {} tarefas para o usuário", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Cria uma nova tarefa.
     * 
     * Validações automáticas via Bean Validation (@Valid).
     * 
     * @param task Dados da tarefa (JSON no body)
     * @return Tarefa criada (201 Created) com header Location
     */
    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody Task task) {
        log.debug("POST /api/tasks - Criando nova tarefa");
        Task saved = service.create(task);
        
        // Retorna 201 Created com a URI do recurso criado
        URI location = URI.create("/api/tasks/" + saved.getId());
        log.info("Tarefa criada - ID: {}", saved.getId());
        
        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Atualiza uma tarefa existente.
     * 
     * Suporta atualização parcial (PATCH-like).
     * Apenas campos fornecidos serão atualizados.
     * 
     * @param id ID da tarefa a ser atualizada
     * @param partial Dados parciais para atualização
     * @return Tarefa atualizada (200 OK) ou 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> update(
            @PathVariable Long id,
            @RequestBody Task partial) {
        
        log.debug("PUT /api/tasks/{} - Atualizando tarefa", id);
        
        try {
            Task updated = service.update(id, partial);
            log.info("Tarefa {} atualizada", id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("Tentativa de atualização de tarefa inexistente: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Exclui uma tarefa do sistema.
     * 
     * @param id ID da tarefa a ser excluída
     * @return 204 No Content (sucesso) ou 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/tasks/{} - Excluindo tarefa", id);
        
        // Verifica se a tarefa existe antes de excluir
        if (service.findById(id).isEmpty()) {
            log.warn("Tentativa de exclusão de tarefa inexistente: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        service.delete(id);
        log.info("Tarefa {} excluída", id);
        
        // Retorna 204 No Content (sucesso sem corpo de resposta)
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de health check.
     * 
     * Verifica se o serviço está respondendo.
     * 
     * @return Status OK com mensagem (200 OK)
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        log.debug("GET /api/tasks/health - Health check");
        return ResponseEntity.ok(new HealthResponse("Task Service is running", "UP"));
    }

    /**
     * Record para resposta de health check.
     */
    record HealthResponse(String message, String status) {}
}
