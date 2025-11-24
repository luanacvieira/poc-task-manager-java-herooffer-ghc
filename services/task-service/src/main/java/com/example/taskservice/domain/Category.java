package com.example.taskservice.domain;

/**
 * Enum representando as categorias de uma tarefa.
 * 
 * Permite organizar tarefas por contexto:
 * - WORK: Tarefas relacionadas ao trabalho
 * - PERSONAL: Tarefas pessoais
 * - STUDY: Tarefas relacionadas a estudos
 * - HEALTH: Tarefas relacionadas à saúde
 * - OTHER: Outras categorias não especificadas
 */
public enum Category {
    WORK,
    PERSONAL,
    STUDY,
    HEALTH,
    OTHER
}
