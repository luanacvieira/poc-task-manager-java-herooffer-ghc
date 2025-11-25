package com.example.taskservice.service;

import com.example.taskservice.domain.Task;
import com.example.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de negócio para gerenciamento de tarefas.
 * 
 * Responsabilidades:
 * - Implementar regras de negócio relacionadas a tarefas
 * - Coordenar operações entre repository e controller
 * - Gerenciar transações de banco de dados
 * 
 * Padrão: Service Layer
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository repository;

    /**
     * Cria uma nova tarefa no sistema.
     * 
     * @param task Dados da tarefa a ser criada
     * @return Tarefa criada com ID gerado
     */
    public Task create(Task task) {
        log.info("Criando nova tarefa: {}", task.getTitle());
        Task saved = repository.save(task);
        log.info("Tarefa criada com ID: {}", saved.getId());
        return saved;
    }

    /**
     * Busca todas as tarefas do sistema.
     * 
     * @return Lista com todas as tarefas
     */
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        log.debug("Buscando todas as tarefas");
        return repository.findAll();
    }

    /**
     * Busca uma tarefa específica por ID.
     * 
     * @param id ID da tarefa
     * @return Optional contendo a tarefa se encontrada
     */
    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        log.debug("Buscando tarefa com ID: {}", id);
        return repository.findById(id);
    }

    /**
     * Busca tarefas de um usuário específico.
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas do usuário
     */
    @Transactional(readOnly = true)
    public List<Task> findByUserId(String userId) {
        log.debug("Buscando tarefas do usuário: {}", userId);
        return repository.findByUserId(userId);
    }

    /**
     * Atualiza uma tarefa existente.
     * 
     * Aplica o padrão de atualização parcial (PATCH),
     * atualizando apenas os campos fornecidos.
     * 
     * @param id ID da tarefa a ser atualizada
     * @param partial Dados parciais para atualização
     * @return Tarefa atualizada
     * @throws IllegalArgumentException se a tarefa não for encontrada
     */
    public Task update(Long id, Task partial) {
        log.info("Atualizando tarefa ID: {}", id);
        
        return repository.findById(id)
            .map(existing -> {
                // Atualiza apenas os campos fornecidos (não-nulos)
                if (partial.getTitle() != null) {
                    existing.setTitle(partial.getTitle());
                }
                if (partial.getDescription() != null) {
                    existing.setDescription(partial.getDescription());
                }
                if (partial.getPriority() != null) {
                    existing.setPriority(partial.getPriority());
                }
                if (partial.getCategory() != null) {
                    existing.setCategory(partial.getCategory());
                }
                if (partial.getDueDate() != null) {
                    existing.setDueDate(partial.getDueDate());
                }
                if (partial.getTags() != null && !partial.getTags().isEmpty()) {
                    existing.setTags(partial.getTags());
                }
                if (partial.getAssignedTo() != null) {
                    existing.setAssignedTo(partial.getAssignedTo());
                }
                // Campo completed sempre atualizado
                existing.setCompleted(partial.isCompleted());
                
                Task updated = repository.save(existing);
                log.info("Tarefa {} atualizada com sucesso", id);
                return updated;
            })
            .orElseThrow(() -> {
                log.error("Tarefa não encontrada com ID: {}", id);
                return new IllegalArgumentException("Task not found: " + id);
            });
    }

    /**
     * Exclui uma tarefa do sistema.
     * 
     * @param id ID da tarefa a ser excluída
     */
    public void delete(Long id) {
        log.info("Excluindo tarefa ID: {}", id);
        repository.deleteById(id);
        log.info("Tarefa {} excluída com sucesso", id);
    }

    /**
     * Retorna o total de tarefas no sistema.
     * 
     * @return Número total de tarefas
     */
    @Transactional(readOnly = true)
    public long total() {
        return repository.count();
    }

    /**
     * Retorna o total de tarefas concluídas.
     * 
     * @return Número de tarefas completas
     */
    @Transactional(readOnly = true)
    public long completed() {
        return repository.countCompleted();
    }

    /**
     * Retorna o total de tarefas pendentes.
     * 
     * @return Número de tarefas pendentes
     */
    @Transactional(readOnly = true)
    public long pending() {
        return repository.countPending();
    }

    /**
     * Retorna o total de tarefas urgentes ativas.
     * 
     * @return Número de tarefas urgentes e não concluídas
     */
    @Transactional(readOnly = true)
    public long urgentActive() {
        return repository.countUrgentActive();
    }
}
