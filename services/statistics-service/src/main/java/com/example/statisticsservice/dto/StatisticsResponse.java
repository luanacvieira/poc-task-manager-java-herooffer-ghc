package com.example.statisticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para resposta de estatísticas agregadas.
 * 
 * Contém métricas e distribuições calculadas a partir das tarefas.
 * 
 * @author Task Manager Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    
    /**
     * Total de tarefas no sistema.
     */
    private Long total;
    
    /**
     * Número de tarefas concluídas.
     */
    private Long completed;
    
    /**
     * Número de tarefas pendentes (não concluídas).
     */
    private Long pending;
    
    /**
     * Número de tarefas urgentes que ainda estão ativas.
     */
    private Long urgentActive;
    
    /**
     * Número de tarefas vencidas (dueDate passou e não foram concluídas).
     */
    private Long overdue;
    
    /**
     * Taxa de conclusão em percentual (0-100).
     */
    private Double completionRate;
    
    /**
     * Distribuição de tarefas por prioridade.
     * Exemplo: {"LOW": 10, "MEDIUM": 20, "HIGH": 15, "URGENT": 5}
     */
    private Map<String, Long> byPriority;
    
    /**
     * Distribuição de tarefas por categoria.
     * Exemplo: {"WORK": 25, "PERSONAL": 15, "STUDY": 10}
     */
    private Map<String, Long> byCategory;
}
