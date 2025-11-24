package com.example.statisticsservice.service;

import com.example.statisticsservice.client.TaskServiceClient;
import com.example.statisticsservice.dto.TaskDto;
import com.example.statisticsservice.dto.StatisticsResponse;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;

/**
 * Testes unitários para StatisticsService.
 * Utiliza Mockito para mockar o TaskServiceClient.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService - Testes Unitários")
class StatisticsServiceTest {

    @Mock
    private TaskServiceClient taskServiceClient;

    @InjectMocks
    private StatisticsService statisticsService;

    private List<TaskDto> sampleTasks;

    @BeforeEach
    void setUp() {
        TaskDto task1 = new TaskDto();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setPriority("HIGH");
        task1.setCategory("WORK");
        task1.setCompleted(true);
        task1.setUserId("user1");
        task1.setDueDate(LocalDate.now().plusDays(5));

        TaskDto task2 = new TaskDto();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setPriority("URGENT");
        task2.setCategory("PERSONAL");
        task2.setCompleted(false);
        task2.setUserId("user1");
        task2.setDueDate(LocalDate.now().minusDays(2));

        TaskDto task3 = new TaskDto();
        task3.setId(3L);
        task3.setTitle("Task 3");
        task3.setPriority("LOW");
        task3.setCategory("WORK");
        task3.setCompleted(false);
        task3.setUserId("user2");
        task3.setDueDate(LocalDate.now().plusDays(10));

        sampleTasks = Arrays.asList(task1, task2, task3);
    }

    @Test
    @DisplayName("Deve calcular estatísticas corretamente")
    void shouldCalculateStatistics() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotal()).isEqualTo(3);
        assertThat(stats.getCompleted()).isEqualTo(1);
        assertThat(stats.getPending()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve contar tarefas urgentes ativas")
    void shouldCountUrgentActiveTasks() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats.getUrgentActive()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve calcular distribuição por prioridade")
    void shouldCalculatePriorityDistribution() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();
        Map<String, Long> byPriority = stats.getByPriority();

        // Assert
        assertThat(byPriority).containsEntry("HIGH", 1L);
        assertThat(byPriority).containsEntry("URGENT", 1L);
        assertThat(byPriority).containsEntry("LOW", 1L);
    }

    @Test
    @DisplayName("Deve calcular distribuição por categoria")
    void shouldCalculateCategoryDistribution() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();
        Map<String, Long> byCategory = stats.getByCategory();

        // Assert
        assertThat(byCategory).containsEntry("WORK", 2L);
        assertThat(byCategory).containsEntry("PERSONAL", 1L);
    }

    @Test
    @DisplayName("Deve identificar tarefas vencidas")
    void shouldIdentifyOverdueTasks() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats.getOverdue()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve calcular taxa de conclusão corretamente")
    void shouldCalculateCompletionRate() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats.getCompletionRate()).isCloseTo(33.33, within(0.1));
    }

    @Test
    @DisplayName("Deve retornar taxa de conclusão 0 para lista vazia")
    void shouldReturnZeroCompletionRateForEmptyList() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(List.of());

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotal()).isZero();
        assertThat(stats.getCompletionRate()).isZero();
    }

    @Test
    @DisplayName("Deve calcular taxa de conclusão 100% quando todas completas")
    void shouldCalculate100PercentWhenAllCompleted() {
        // Arrange
        sampleTasks.forEach(task -> task.setCompleted(true));
        when(taskServiceClient.getAllTasks()).thenReturn(sampleTasks);

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats.getCompleted()).isEqualTo(3);
        assertThat(stats.getPending()).isZero();
        assertThat(stats.getCompletionRate()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Deve retornar estatísticas vazias quando não há tarefas")
    void shouldReturnEmptyStatisticsWhenNoTasks() {
        // Arrange
        when(taskServiceClient.getAllTasks()).thenReturn(List.of());

        // Act
        StatisticsResponse stats = statisticsService.getStatistics();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getTotal()).isZero();
        assertThat(stats.getCompleted()).isZero();
        assertThat(stats.getPending()).isZero();
    }
}
