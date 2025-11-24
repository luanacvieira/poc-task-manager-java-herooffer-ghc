package com.example.taskservice.controller;

import com.example.taskservice.domain.Category;
import com.example.taskservice.domain.Priority;
import com.example.taskservice.domain.Task;
import com.example.taskservice.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para TaskController.
 * Utiliza contexto Spring completo e banco H2 em memória.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("TaskController - Testes de Integração")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setTitle("Test Task");
        sampleTask.setDescription("Description");
        sampleTask.setPriority(Priority.HIGH);
        sampleTask.setCategory(Category.WORK);
        sampleTask.setUserId("user123");
        sampleTask.setDueDate(LocalDate.now().plusDays(7));
    }

    @Test
    @DisplayName("GET /api/tasks - Deve listar todas as tarefas")
    void shouldListAllTasks() throws Exception {
        // Arrange
        taskService.create(sampleTask);

        // Act & Assert
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].title", is("Test Task")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Deve buscar tarefa por ID")
    void shouldGetTaskById() throws Exception {
        // Arrange
        Task saved = taskService.create(sampleTask);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    @DisplayName("GET /api/tasks/{id} - Deve retornar 404 quando tarefa não existe")
    void shouldReturn404WhenTaskNotFound() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/tasks - Deve criar nova tarefa")
    void shouldCreateTask() throws Exception {
        // Arrange
        String taskJson = objectMapper.writeValueAsString(sampleTask);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    @DisplayName("POST /api/tasks - Deve rejeitar tarefa sem título")
    void shouldRejectTaskWithoutTitle() throws Exception {
        // Arrange
        sampleTask.setTitle(null);
        String taskJson = objectMapper.writeValueAsString(sampleTask);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Deve atualizar tarefa existente")
    void shouldUpdateTask() throws Exception {
        // Arrange
        Task saved = taskService.create(sampleTask);
        saved.setTitle("Updated Title");
        String taskJson = objectMapper.writeValueAsString(saved);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")));
    }

    @Test
    @DisplayName("PUT /api/tasks/{id} - Deve retornar 404 ao atualizar tarefa inexistente")
    void shouldReturn404WhenUpdatingNonExistentTask() throws Exception {
        // Arrange
        String taskJson = objectMapper.writeValueAsString(sampleTask);

        // Act & Assert
        mockMvc.perform(put("/api/tasks/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Deve deletar tarefa existente")
    void shouldDeleteTask() throws Exception {
        // Arrange
        Task saved = taskService.create(sampleTask);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/tasks/{id} - Deve retornar 404 ao deletar tarefa inexistente")
    void shouldReturn404WhenDeletingNonExistentTask() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/tasks/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/tasks/user/{userId} - Deve buscar tarefas por userId")
    void shouldGetTasksByUserId() throws Exception {
        // Arrange
        taskService.create(sampleTask);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setUserId("user456");
        task2.setPriority(Priority.LOW);
        task2.setCategory(Category.PERSONAL);
        taskService.create(task2);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/user/user123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is("user123")));
    }

    @Test
    @DisplayName("GET /api/tasks/health - Deve retornar status UP")
    void shouldReturnHealthStatus() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/tasks/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.message", containsString("Task Service")));
    }
}
