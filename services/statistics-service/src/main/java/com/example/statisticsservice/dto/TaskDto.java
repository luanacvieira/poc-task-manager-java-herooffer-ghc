package com.example.statisticsservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO (Data Transfer Object) representando uma tarefa.
 * 
 * Usado para transferir dados entre o Task Service e o Statistics Service.
 * Espelha a estrutura da entidade Task, mas sem anotações JPA.
 * 
 * Padrão: DTO Pattern (para desacoplamento entre serviços)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    
    private Long id;
    private String title;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String category; // WORK, PERSONAL, STUDY, HEALTH, OTHER
    private LocalDate dueDate;
    private Set<String> tags;
    private String assignedTo;
    private String userId;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
