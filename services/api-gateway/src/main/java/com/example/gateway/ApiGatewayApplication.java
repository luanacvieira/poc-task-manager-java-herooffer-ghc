package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do API Gateway.
 * 
 * Este serviço funciona como porta de entrada única (Single Entry Point)
 * para todos os microsserviços da aplicação.
 * 
 * Responsabilidades:
 * - Rotear requisições HTTP para os microsserviços apropriados
 * - Fornecer ponto de entrada unificado (http://localhost:8080)
 * - Facilitar CORS, autenticação e logging centralizados (futuro)
 * - Balanceamento de carga (se múltiplas instâncias)
 * 
 * Padrão: API Gateway Pattern
 * 
 * Rotas configuradas:
 * - /api/tasks/**      -> Task Service (8081)
 * - /api/statistics/** -> Statistics Service (8082)
 * 
 * Portas:
 * - Gateway: 8080 (porta externa, mesma do monólito original)
 * - Actuator: 8080/actuator
 * 
 * @author Task Manager Team
 * @version 1.0.0
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
