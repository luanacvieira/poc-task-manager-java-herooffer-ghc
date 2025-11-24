package com.example.statisticsservice.client;

import com.example.statisticsservice.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Cliente HTTP para comunicação com o Task Service.
 * 
 * Responsável por:
 * - Fazer chamadas REST para o Task Service
 * - Buscar dados de tarefas para agregação de estatísticas
 * - Tratar erros de comunicação entre serviços
 * 
 * Padrão: Service Client / API Gateway Pattern
 * 
 * @author Task Manager Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskServiceClient {

    private final WebClient.Builder webClientBuilder;

    /**
     * URL base do Task Service, configurada via properties.
     * Exemplo: http://task-service:8081
     */
    @Value("${task.service.url}")
    private String taskServiceUrl;

    /**
     * Busca todas as tarefas do Task Service.
     * 
     * Faz uma chamada HTTP GET para /api/tasks e converte
     * o resultado em uma lista de TaskDto.
     * 
     * @return Lista de tarefas (pode estar vazia em caso de erro)
     */
    public List<TaskDto> getAllTasks() {
        log.info("Buscando todas as tarefas do Task Service: {}", taskServiceUrl);
        
        try {
            // Cria WebClient com a URL base do serviço
            WebClient webClient = webClientBuilder.baseUrl(taskServiceUrl).build();
            
            // Faz requisição GET para /api/tasks
            List<TaskDto> tasks = webClient.get()
                    .uri("/api/tasks")
                    .retrieve()
                    // Converte resposta para List<TaskDto>
                    .bodyToFlux(TaskDto.class)
                    .collectList()
                    // Bloqueia para obter resultado síncrono
                    .block();
            
            log.info("Recebidas {} tarefas do Task Service", tasks != null ? tasks.size() : 0);
            return tasks != null ? tasks : List.of();
            
        } catch (Exception e) {
            log.error("Erro ao buscar tarefas do Task Service: {}", e.getMessage(), e);
            // Retorna lista vazia em caso de falha (circuit breaker simplificado)
            return List.of();
        }
    }

    /**
     * Verifica se o Task Service está disponível.
     * 
     * Faz uma chamada ao endpoint de health check.
     * 
     * @return true se o serviço está UP, false caso contrário
     */
    public boolean isTaskServiceAvailable() {
        try {
            WebClient webClient = webClientBuilder.baseUrl(taskServiceUrl).build();
            
            String status = webClient.get()
                    .uri("/api/tasks/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            boolean available = status != null && status.contains("UP");
            log.info("Task Service disponível: {}", available);
            return available;
            
        } catch (Exception e) {
            log.warn("Task Service indisponível: {}", e.getMessage());
            return false;
        }
    }
}
