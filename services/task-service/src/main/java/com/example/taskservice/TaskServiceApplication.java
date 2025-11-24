package com.example.taskservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do Task Service.
 * 
 * Este microsserviço é responsável por:
 * - Gerenciar o ciclo de vida completo das tarefas (CRUD)
 * - Armazenar dados de tarefas em banco próprio (Database per Service)
 * - Expor API REST para operações de tarefas
 * 
 * Portas:
 * - Aplicação: 8081
 * - Actuator: 8081/actuator
 * 
 * @author Task Manager Team
 * @version 1.0.0
 */
@SpringBootApplication
public class TaskServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskServiceApplication.class, args);
    }
}
