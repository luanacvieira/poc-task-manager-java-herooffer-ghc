package com.example.gateway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Testes de integração para HealthCheckController.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("HealthCheckController - Testes de Integração")
class HealthCheckControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET /healthcheck - Deve retornar status 200 OK")
    void shouldReturnOkStatus() {
        webTestClient.get()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo("ok");
    }

    @Test
    @DisplayName("GET /healthcheck - Deve retornar JSON válido")
    void shouldReturnValidJson() {
        webTestClient.get()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("{\"status\":\"ok\"}");
    }

    @Test
    @DisplayName("POST /healthcheck - Deve retornar 405 Method Not Allowed")
    void shouldReturn405ForPost() {
        webTestClient.post()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isEqualTo(405);
    }

    @Test
    @DisplayName("PUT /healthcheck - Deve retornar 405 Method Not Allowed")
    void shouldReturn405ForPut() {
        webTestClient.put()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isEqualTo(405);
    }

    @Test
    @DisplayName("DELETE /healthcheck - Deve retornar 405 Method Not Allowed")
    void shouldReturn405ForDelete() {
        webTestClient.delete()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isEqualTo(405);
    }

    @Test
    @DisplayName("GET /healthcheck - Deve responder rapidamente")
    void shouldRespondQuickly() {
        long startTime = System.currentTimeMillis();
        
        webTestClient.get()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isOk();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Deve responder em menos de 1 segundo
        assert duration < 1000;
    }

    @Test
    @DisplayName("GET /healthcheck - Deve ser idempotente")
    void shouldBeIdempotent() {
        // Executa múltiplas vezes e verifica que sempre retorna o mesmo resultado
        for (int i = 0; i < 5; i++) {
            webTestClient.get()
                    .uri("/healthcheck")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo("ok");
        }
    }

    @Test
    @DisplayName("GET /healthcheck - Deve ter Content-Type application/json")
    void shouldHaveJsonContentType() {
        webTestClient.get()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }
}
