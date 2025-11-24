package com.example.statisticsservice.service;

import com.example.statisticsservice.client.TaskServiceClient;
import com.example.statisticsservice.dto.TaskDto;
import com.example.statisticsservice.dto.StatisticsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço de agregação e cálculo de estatísticas.
 * 
 * Responsabilidades:
 * - Consumir dados do Task Service
 * - Calcular métricas agregadas (totais, médias, distribuições)
 * - Fornecer insights sobre tarefas
 * 
 * Padrão: Service Layer + Aggregation Pattern
 * 
 * @author Task Manager Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final TaskServiceClient taskServiceClient;

    /**
     * Calcula e retorna estatísticas gerais das tarefas.
     * 
     * Inclui:
     * - Total de tarefas
     * - Tarefas concluídas e pendentes
     * - Tarefas urgentes ativas
     * - Distribuição por prioridade
     * - Distribuição por categoria
     * - Tarefas vencidas
     * 
     * @return Objeto com todas as estatísticas calculadas
     */
    public StatisticsResponse getStatistics() {
        log.info("Calculando estatísticas de tarefas");
        
        // Busca todas as tarefas do Task Service
        List<TaskDto> tasks = taskServiceClient.getAllTasks();
        
        if (tasks.isEmpty()) {
            log.warn("Nenhuma tarefa encontrada para calcular estatísticas");
            return createEmptyStatistics();
        }

        // Cálculos de estatísticas
        long total = tasks.size();
        long completed = tasks.stream().filter(TaskDto::isCompleted).count();
        long pending = total - completed;
        long urgentActive = tasks.stream()
                .filter(t -> !t.isCompleted() && "URGENT".equals(t.getPriority()))
                .count();
        long overdue = tasks.stream()
                .filter(this::isOverdue)
                .count();

        // Distribuição por prioridade
        Map<String, Long> byPriority = tasks.stream()
                .collect(Collectors.groupingBy(
                        TaskDto::getPriority,
                        Collectors.counting()
                ));

        // Distribuição por categoria
        Map<String, Long> byCategory = tasks.stream()
                .collect(Collectors.groupingBy(
                        TaskDto::getCategory,
                        Collectors.counting()
                ));

        // Taxa de conclusão
        double completionRate = total > 0 ? (double) completed / total * 100 : 0.0;

        log.info("Estatísticas calculadas: {} tarefas, {:.2f}% concluídas", total, completionRate);

        return StatisticsResponse.builder()
                .total(total)
                .completed(completed)
                .pending(pending)
                .urgentActive(urgentActive)
                .overdue(overdue)
                .completionRate(completionRate)
                .byPriority(byPriority)
                .byCategory(byCategory)
                .build();
    }

    /**
     * Verifica se uma tarefa está vencida.
     * 
     * Uma tarefa é considerada vencida se:
     * - Não está concluída
     * - Possui data de vencimento
     * - A data de vencimento é anterior à data atual
     * 
     * @param task Tarefa a ser verificada
     * @return true se a tarefa está vencida
     */
    private boolean isOverdue(TaskDto task) {
        if (task.isCompleted() || task.getDueDate() == null) {
            return false;
        }
        return task.getDueDate().isBefore(LocalDate.now());
    }

    /**
     * Cria um objeto de estatísticas vazio.
     * 
     * Usado quando não há tarefas disponíveis.
     * 
     * @return Estatísticas zeradas
     */
    private StatisticsResponse createEmptyStatistics() {
        return StatisticsResponse.builder()
                .total(0L)
                .completed(0L)
                .pending(0L)
                .urgentActive(0L)
                .overdue(0L)
                .completionRate(0.0)
                .byPriority(Map.of())
                .byCategory(Map.of())
                .build();
    }

    /**
     * Verifica a disponibilidade do Task Service.
     * 
     * @return Status de disponibilidade
     */
    public boolean isTaskServiceAvailable() {
        return taskServiceClient.isTaskServiceAvailable();
    }
}
