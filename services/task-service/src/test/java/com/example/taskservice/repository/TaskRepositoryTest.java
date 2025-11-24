package com.example.taskservice.repository;

import com.example.taskservice.domain.Category;
import com.example.taskservice.domain.Priority;
import com.example.taskservice.domain.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes para TaskRepository.
 * Utiliza @DataJpaTest para configurar um contexto JPA mínimo.
 */
@DataJpaTest
@DisplayName("TaskRepository - Testes de Persistência")
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setTitle("Tarefa de Teste");
        sampleTask.setDescription("Descrição");
        sampleTask.setPriority(Priority.HIGH);
        sampleTask.setCategory(Category.WORK);
        sampleTask.setUserId("user123");
        sampleTask.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("Deve salvar e recuperar tarefa")
    void shouldSaveAndRetrieveTask() {
        // Act
        Task saved = taskRepository.save(sampleTask);
        Task found = entityManager.find(Task.class, saved.getId());

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getTitle()).isEqualTo("Tarefa de Teste");
        assertThat(found.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("Deve contar tarefas completadas")
    void shouldCountCompletedTasks() {
        // Arrange
        sampleTask.setCompleted(true);
        taskRepository.save(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Tarefa 2");
        task2.setUserId("user123");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);
        task2.setCompleted(false);
        taskRepository.save(task2);

        // Act
        long completed = taskRepository.countCompleted();

        // Assert
        assertThat(completed).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve contar tarefas pendentes")
    void shouldCountPendingTasks() {
        // Arrange
        taskRepository.save(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Tarefa 2");
        task2.setUserId("user123");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);
        task2.setCompleted(true);
        taskRepository.save(task2);

        // Act
        long pending = taskRepository.countPending();

        // Assert
        assertThat(pending).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve contar tarefas urgentes ativas")
    void shouldCountUrgentActiveTasks() {
        // Arrange
        sampleTask.setPriority(Priority.URGENT);
        sampleTask.setCompleted(false);
        taskRepository.save(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Tarefa 2");
        task2.setUserId("user123");
        task2.setPriority(Priority.URGENT);
        task2.setCategory(Category.PERSONAL);
        task2.setCompleted(true);
        taskRepository.save(task2);

        // Act
        long urgentActive = taskRepository.countUrgentActive();

        // Assert
        assertThat(urgentActive).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve buscar tarefas por userId")
    void shouldFindTasksByUserId() {
        // Arrange
        taskRepository.save(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Tarefa 2");
        task2.setUserId("user456");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);
        taskRepository.save(task2);

        // Act
        List<Task> tasks = taskRepository.findByUserId("user123");

        // Assert
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Tarefa de Teste");
    }

    @Test
    @DisplayName("Deve buscar tarefas por categoria")
    void shouldFindTasksByCategory() {
        // Arrange
        taskRepository.save(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Tarefa 2");
        task2.setUserId("user123");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);
        taskRepository.save(task2);

        // Act
        List<Task> tasks = taskRepository.findByCategory(Category.WORK);

        // Assert
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getCategory()).isEqualTo(Category.WORK);
    }

    @Test
    @DisplayName("Deve deletar tarefa por ID")
    void shouldDeleteTaskById() {
        // Arrange
        Task saved = taskRepository.save(sampleTask);
        Long id = saved.getId();

        // Act
        taskRepository.deleteById(id);
        entityManager.flush();

        // Assert
        Task found = entityManager.find(Task.class, id);
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Deve contar total de tarefas")
    void shouldCountAllTasks() {
        // Arrange
        taskRepository.save(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Tarefa 2");
        task2.setUserId("user123");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);
        taskRepository.save(task2);

        // Act
        long count = taskRepository.count();

        // Assert
        assertThat(count).isEqualTo(2);
    }
}
