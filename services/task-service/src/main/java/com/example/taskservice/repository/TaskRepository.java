package com.example.taskservice.repository;

import com.example.taskservice.domain.Category;
import com.example.taskservice.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para acesso a dados de tarefas.
 * 
 * Utiliza Spring Data JPA para abstração de persistência,
 * eliminando a necessidade de implementação manual de DAOs.
 * 
 * Padrão: Repository Pattern
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Busca todas as tarefas de um usuário específico.
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas do usuário
     */
    List<Task> findByUserId(String userId);

    /**
     * Conta o total de tarefas concluídas.
     * 
     * @return Número de tarefas completas
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.completed = true")
    long countCompleted();

    /**
     * Conta o total de tarefas pendentes (não concluídas).
     * 
     * @return Número de tarefas pendentes
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.completed = false")
    long countPending();

    /**
     * Conta tarefas urgentes que ainda estão ativas.
     * 
     * @return Número de tarefas urgentes e não concluídas
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.priority = 'URGENT' AND t.completed = false")
    long countUrgentActive();
    
    /**
     * Busca tarefas por categoria.
     * 
     * @param category Categoria desejada
     * @return Lista de tarefas da categoria
     */
    List<Task> findByCategory(Category category);
}
