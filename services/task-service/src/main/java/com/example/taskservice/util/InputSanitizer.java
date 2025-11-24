package com.example.taskservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Utilitário para sanitização de entradas de usuário.
 * 
 * Previne ataques de:
 * - XSS (Cross-Site Scripting)
 * - SQL Injection (camada adicional)
 * - Log Injection
 * - Path Traversal
 * 
 * OWASP: Input Validation
 * CWE-79, CWE-89, CWE-117, CWE-22
 */
@Component
@Slf4j
public class InputSanitizer {

    // Padrões de ataque comuns
    private static final Pattern XSS_SCRIPT = Pattern.compile(
            "<script|</script|javascript:|onerror=|onload=|onclick=|<iframe|eval\\(",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SQL_INJECTION = Pattern.compile(
            "('(''|[^'])*')|(;)|(\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE)?|INSERT( +INTO)?|MERGE|SELECT|UPDATE|UNION( +ALL)?)\\b)",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern LOG_INJECTION = Pattern.compile(
            "[\\r\\n]",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PATH_TRAVERSAL = Pattern.compile(
            "\\.\\./|\\.\\.\\\\",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitiza texto geral removendo scripts e caracteres perigosos.
     * 
     * @param input Texto a ser sanitizado
     * @return Texto limpo ou null se entrada for null
     */
    public String sanitizeText(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        String clean = input;

        // Remove scripts XSS
        if (XSS_SCRIPT.matcher(clean).find()) {
            log.warn("Tentativa de XSS detectada e bloqueada");
            clean = XSS_SCRIPT.matcher(clean).replaceAll("");
        }

        // Remove SQL injection patterns
        if (SQL_INJECTION.matcher(clean).find()) {
            log.warn("Tentativa de SQL Injection detectada e bloqueada");
            clean = SQL_INJECTION.matcher(clean).replaceAll("");
        }

        // Remove path traversal
        if (PATH_TRAVERSAL.matcher(clean).find()) {
            log.warn("Tentativa de Path Traversal detectada e bloqueada");
            clean = PATH_TRAVERSAL.matcher(clean).replaceAll("");
        }

        // Normaliza espaços
        clean = clean.trim().replaceAll("\\s+", " ");

        return clean;
    }

    /**
     * Sanitiza entrada para logs, removendo quebras de linha.
     * 
     * Previne Log Injection (CWE-117).
     * 
     * @param input Texto para log
     * @return Texto sem quebras de linha
     */
    public String sanitizeForLog(String input) {
        if (input == null) {
            return "null";
        }

        // Remove quebras de linha que podem permitir injeção em logs
        String clean = LOG_INJECTION.matcher(input).replaceAll("_");

        // Limita tamanho para logs
        if (clean.length() > 100) {
            clean = clean.substring(0, 100) + "...";
        }

        return clean;
    }

    /**
     * Sanitiza user ID, permitindo apenas alfanuméricos e hífens.
     * 
     * @param userId ID do usuário
     * @return User ID limpo
     * @throws IllegalArgumentException se formato inválido
     */
    public String sanitizeUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID não pode ser vazio");
        }

        // Remove caracteres não permitidos
        String clean = userId.replaceAll("[^a-zA-Z0-9-_]", "");

        // Valida tamanho
        if (clean.length() < 3 || clean.length() > 50) {
            throw new IllegalArgumentException("User ID deve ter entre 3 e 50 caracteres");
        }

        if (!clean.equals(userId)) {
            log.warn("User ID sanitizado: {} -> {}", 
                    sanitizeForLog(userId), sanitizeForLog(clean));
        }

        return clean;
    }

    /**
     * Valida e sanitiza tag, permitindo apenas lowercase, números e hífens.
     * 
     * @param tag Tag a ser validada
     * @return Tag sanitizada
     * @throws IllegalArgumentException se formato inválido
     */
    public String sanitizeTag(String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag não pode ser vazia");
        }

        // Converte para lowercase e remove caracteres inválidos
        String clean = tag.toLowerCase().replaceAll("[^a-z0-9-]", "");

        if (clean.length() < 2 || clean.length() > 20) {
            throw new IllegalArgumentException("Tag deve ter entre 2 e 20 caracteres");
        }

        return clean;
    }

    /**
     * Mascara dados sensíveis para logs (ex: emails, IDs parciais).
     * 
     * @param sensitive Dado sensível
     * @return Dado mascarado
     */
    public String maskSensitiveData(String sensitive) {
        if (sensitive == null || sensitive.length() < 4) {
            return "****";
        }

        // Mostra apenas os 4 primeiros caracteres
        return sensitive.substring(0, 4) + "****";
    }
}
