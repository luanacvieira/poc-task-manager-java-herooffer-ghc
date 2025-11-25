package com.example.taskservice.domain;

/**
 * Enum representando os níveis de prioridade de uma tarefa.
 * 
 * Ordenados do menor para o maior nível de urgência:
 * - LOW: Prioridade baixa, pode ser feito quando houver tempo
 * - MEDIUM: Prioridade média, deve ser feito em breve
 * - HIGH: Prioridade alta, requer atenção prioritária
 * - URGENT: Urgente, requer ação imediata
 */
public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}
