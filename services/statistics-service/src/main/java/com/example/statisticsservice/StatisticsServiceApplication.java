package com.example.statisticsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Ponto de entrada do Statistics Service.
 * 
 * Este microsserviço é responsável por:
 * - Agregar estatísticas de tarefas de múltiplos serviços
 * - Consumir dados do Task Service via REST
 * - Calcular métricas e KPIs relacionados a tarefas
 * - Fornecer endpoints de consulta de estatísticas
 * 
 * Padrão: Agregador de Dados (Data Aggregator Pattern)
 * 
 * Portas:
 * - Aplicação: 8082
 * - Actuator: 8082/actuator
 * 
 * @author Task Manager Team
 * @version 1.0.0
 */
@SpringBootApplication
public class StatisticsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsServiceApplication.class, args);
    }

    /**
     * Configura WebClient para comunicação HTTP com outros serviços.
     * 
     * WebClient é o cliente HTTP reativo do Spring (substituto do RestTemplate).
     * Permite fazer chamadas HTTP de forma não-bloqueante.
     * 
     * @return WebClient.Builder configurado
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
