package com.example.taskmanager.web.rest;

import com.example.taskmanager.domain.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDto toDto(Task t) {
        return new TaskDto(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getPriority(),
                t.getCategory(),
                t.getDueDate(),
                t.getTags(),
                t.getAssignedTo(),
                t.getUserId(),
                t.isCompleted(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
