package com.example.taskservice.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes unitários para a entidade Task.
 * Verifica comportamento básico dos getters/setters e defaults.
 */
@DisplayName("Task Entity - Testes Unitários")
class TaskTest {

    @Test
    @DisplayName("Deve criar tarefa com valores padrão")
    void shouldCreateTaskWithDefaultValues() {
        // Act
        Task task = new Task();
        task.setTitle("Test Task");
        task.setUserId("user1");

        // Assert
        assertThat(task.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(task.getCategory()).isEqualTo(Category.OTHER);
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.getTags()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Deve configurar título corretamente")
    void shouldSetTitle() {
        // Arrange
        Task task = new Task();

        // Act
        task.setTitle("Nova Tarefa");

        // Assert
        assertThat(task.getTitle()).isEqualTo("Nova Tarefa");
    }

    @Test
    @DisplayName("Deve configurar descrição")
    void shouldSetDescription() {
        // Arrange
        Task task = new Task();

        // Act
        task.setDescription("Descrição detalhada");

        // Assert
        assertThat(task.getDescription()).isEqualTo("Descrição detalhada");
    }

    @Test
    @DisplayName("Deve configurar prioridade")
    void shouldSetPriority() {
        // Arrange
        Task task = new Task();

        // Act
        task.setPriority(Priority.URGENT);

        // Assert
        assertThat(task.getPriority()).isEqualTo(Priority.URGENT);
    }

    @Test
    @DisplayName("Deve configurar categoria")
    void shouldSetCategory() {
        // Arrange
        Task task = new Task();

        // Act
        task.setCategory(Category.WORK);

        // Assert
        assertThat(task.getCategory()).isEqualTo(Category.WORK);
    }

    @Test
    @DisplayName("Deve configurar data de vencimento")
    void shouldSetDueDate() {
        // Arrange
        Task task = new Task();
        LocalDate dueDate = LocalDate.now().plusDays(7);

        // Act
        task.setDueDate(dueDate);

        // Assert
        assertThat(task.getDueDate()).isEqualTo(dueDate);
    }

    @Test
    @DisplayName("Deve gerenciar tags")
    void shouldManageTags() {
        // Arrange
        Task task = new Task();
        Set<String> tags = new HashSet<>();
        tags.add("importante");
        tags.add("urgente");

        // Act
        task.setTags(tags);

        // Assert
        assertThat(task.getTags()).hasSize(2);
        assertThat(task.getTags()).contains("importante", "urgente");
    }

    @Test
    @DisplayName("Deve marcar tarefa como completa")
    void shouldMarkTaskAsCompleted() {
        // Arrange
        Task task = new Task();

        // Act
        task.setCompleted(true);

        // Assert
        assertThat(task.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Deve aceitar campos anuláveis")
    void shouldAllowNullableFields() {
        // Arrange
        Task task = new Task();
        task.setTitle("Título");
        task.setUserId("user1");

        // Act
        task.setDescription(null);
        task.setDueDate(null);
        task.setAssignedTo(null);

        // Assert
        assertThat(task.getDescription()).isNull();
        assertThat(task.getDueDate()).isNull();
        assertThat(task.getAssignedTo()).isNull();
    }
}
