package com.example.statisticsservice.controller;

import com.example.statisticsservice.dto.StatisticsResponse;
import com.example.statisticsservice.service.StatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para StatisticsController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("StatisticsController - Testes de Integração")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @DisplayName("GET /api/statistics - Deve retornar estatísticas")
    void shouldReturnStatistics() throws Exception {
        // Arrange
        StatisticsResponse stats = StatisticsResponse.builder()
                .total(10L)
                .completed(6L)
                .pending(4L)
                .urgentActive(2L)
                .overdue(1L)
                .completionRate(60.0)
                .byPriority(Map.of("HIGH", 3L, "LOW", 7L))
                .byCategory(Map.of("WORK", 6L, "PERSONAL", 4L))
                .build();

        when(statisticsService.isTaskServiceAvailable()).thenReturn(true);
        when(statisticsService.getStatistics()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(10)))
                .andExpect(jsonPath("$.completed", is(6)))
                .andExpect(jsonPath("$.pending", is(4)))
                .andExpect(jsonPath("$.urgentActive", is(2)))
                .andExpect(jsonPath("$.completionRate", is(60.0)));
    }

    @Test
    @DisplayName("GET /api/statistics - Deve retornar estatísticas vazias quando Task Service indisponível")
    void shouldReturnEmptyStatisticsWhenServiceUnavailable() throws Exception {
        // Arrange
        StatisticsResponse emptyStats = StatisticsResponse.builder()
                .total(0L)
                .completed(0L)
                .pending(0L)
                .urgentActive(0L)
                .overdue(0L)
                .completionRate(0.0)
                .byPriority(Map.of())
                .byCategory(Map.of())
                .build();

        when(statisticsService.isTaskServiceAvailable()).thenReturn(false);
        when(statisticsService.getStatistics()).thenReturn(emptyStats);

        // Act & Assert
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(0)));
    }

    @Test
    @DisplayName("GET /api/statistics/health - Deve retornar status UP")
    void shouldReturnHealthStatus() throws Exception {
        // Arrange
        when(statisticsService.isTaskServiceAvailable()).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/statistics/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("UP")))
                .andExpect(jsonPath("$.service", containsString("Statistics Service")));
    }
}
