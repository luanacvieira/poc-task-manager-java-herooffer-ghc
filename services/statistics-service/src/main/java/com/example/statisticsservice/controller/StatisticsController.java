package com.example.statisticsservice.controller;

import com.example.statisticsservice.dto.StatisticsResponse;
import com.example.statisticsservice.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para endpoints de estatísticas.
 * 
 * Expõe APIs para consulta de métricas agregadas de tarefas.
 * 
 * Base URL: /api/statistics
 * 
 * Endpoints disponíveis:
 * - GET /api/statistics        - Retorna estatísticas gerais
 * - GET /api/statistics/health - Health check do serviço
 * 
 * @author Task Manager Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Retorna estatísticas agregadas de todas as tarefas.
     * 
     * Consulta o Task Service, processa os dados e retorna métricas
     * como totais, distribuições e taxas de conclusão.
     * 
     * @return Estatísticas calculadas (200 OK)
     */
    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics() {
        log.info("GET /api/statistics - Buscando estatísticas");
        
        // Verifica se o Task Service está disponível
        if (!statisticsService.isTaskServiceAvailable()) {
            log.warn("Task Service indisponível. Retornando estatísticas vazias.");
            // Em produção, poderia retornar 503 Service Unavailable
        }
        
        StatisticsResponse stats = statisticsService.getStatistics();
        log.info("Estatísticas retornadas: {} tarefas totais", stats.getTotal());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint de health check do serviço.
     * 
     * Verifica se o serviço está ativo e se consegue se comunicar
     * com o Task Service.
     * 
     * @return Status do serviço (200 OK)
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("GET /api/statistics/health - Health check");
        
        boolean taskServiceAvailable = statisticsService.isTaskServiceAvailable();
        
        Map<String, Object> health = Map.of(
            "service", "Statistics Service",
            "status", "UP",
            "taskServiceAvailable", taskServiceAvailable
        );
        
        return ResponseEntity.ok(health);
    }
}
