package com.example.taskmanager.web.rest;

import com.example.taskmanager.domain.Category;
import com.example.taskmanager.domain.Priority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record TaskDto(
        Long id,
        String title,
        String description,
        Priority priority,
        Category category,
        LocalDate dueDate,
        Set<String> tags,
        String assignedTo,
        String userId,
        boolean completed,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
