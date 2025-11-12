package com.example.taskmanager.web.rest;

import com.example.taskmanager.domain.Task;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin // permitir evolução futura para frontend separado
public class TaskController {

    private final TaskService service;
    private final TaskMapper mapper;

    public TaskController(TaskService service, TaskMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<TaskDto> list() {
        return service.findAll().stream().map(mapper::toDto).toList();
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody Task task) {
        Task saved = service.create(task);
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.getId())).body(mapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public TaskDto update(@PathVariable Long id, @RequestBody Task partial) {
        Task updated = service.update(id, partial);
        return mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        return ResponseEntity.ok(new StatsResponse(service.total(), service.pending(), service.completed(), service.urgentActive()));
    }

    record StatsResponse(long total, long pending, long completed, long urgent) {}
}
