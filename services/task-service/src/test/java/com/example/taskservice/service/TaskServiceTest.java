package com.example.taskservice.service;

import com.example.taskservice.domain.Category;
import com.example.taskservice.domain.Priority;
import com.example.taskservice.domain.Task;
import com.example.taskservice.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TaskService.
 * Utiliza Mockito para mockar dependências.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService - Testes Unitários")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Description");
        sampleTask.setPriority(Priority.HIGH);
        sampleTask.setCategory(Category.WORK);
        sampleTask.setUserId("user123");
        sampleTask.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("Deve criar tarefa com sucesso")
    void shouldCreateTask() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        Task created = taskService.create(sampleTask);

        // Assert
        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(1L);
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void shouldFindAllTasks() {
        // Arrange
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setUserId("user123");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);

        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask, task2));

        // Act
        List<Task> tasks = taskService.findAll();

        // Assert
        assertThat(tasks).hasSize(2);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID")
    void shouldFindTaskById() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // Act
        Optional<Task> found = taskService.findById(1L);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Task");
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando tarefa não existe")
    void shouldReturnEmptyWhenTaskNotFound() {
        // Arrange
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Task> found = taskService.findById(99L);

        // Assert
        assertThat(found).isEmpty();
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve buscar tarefas por userId")
    void shouldFindTasksByUserId() {
        // Arrange
        when(taskRepository.findByUserId("user123")).thenReturn(Arrays.asList(sampleTask));

        // Act
        List<Task> tasks = taskService.findByUserId("user123");

        // Assert
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getUserId()).isEqualTo("user123");
        verify(taskRepository, times(1)).findByUserId("user123");
    }

    @Test
    @DisplayName("Deve atualizar tarefa existente")
    void shouldUpdateExistingTask() {
        // Arrange
        Task updates = new Task();
        updates.setTitle("Updated Title");
        updates.setDescription("Updated Description");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        // Act
        Task updated = taskService.update(1L, updates);

        // Assert
        assertThat(updated).isNotNull();
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar tarefa inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        // Arrange
        Task updates = new Task();
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.update(99L, updates))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Task not found");

        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve deletar tarefa")
    void shouldDeleteTask() {
        // Arrange
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.delete(1L);

        // Assert
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve retornar total de tarefas")
    void shouldReturnTotalTasks() {
        // Arrange
        when(taskRepository.count()).thenReturn(5L);

        // Act
        long total = taskService.total();

        // Assert
        assertThat(total).isEqualTo(5L);
        verify(taskRepository, times(1)).count();
    }

    @Test
    @DisplayName("Deve retornar total de tarefas completadas")
    void shouldReturnCompletedCount() {
        // Arrange
        when(taskRepository.countCompleted()).thenReturn(3L);

        // Act
        long completed = taskService.completed();

        // Assert
        assertThat(completed).isEqualTo(3L);
        verify(taskRepository, times(1)).countCompleted();
    }

    @Test
    @DisplayName("Deve retornar total de tarefas pendentes")
    void shouldReturnPendingCount() {
        // Arrange
        when(taskRepository.countPending()).thenReturn(2L);

        // Act
        long pending = taskService.pending();

        // Assert
        assertThat(pending).isEqualTo(2L);
        verify(taskRepository, times(1)).countPending();
    }

    @Test
    @DisplayName("Deve retornar total de tarefas urgentes ativas")
    void shouldReturnUrgentActiveCount() {
        // Arrange
        when(taskRepository.countUrgentActive()).thenReturn(1L);

        // Act
        long urgentActive = taskService.urgentActive();

        // Assert
        assertThat(urgentActive).isEqualTo(1L);
        verify(taskRepository, times(1)).countUrgentActive();
    }
}
