package com.example.taskmanager.service;

import com.example.taskmanager.domain.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task create(Task task) {
        return repository.save(task);
    }

    public List<Task> findAll() {
        return repository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    public Task update(Long id, Task partial) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(partial.getTitle() != null ? partial.getTitle() : existing.getTitle());
            existing.setDescription(partial.getDescription() != null ? partial.getDescription() : existing.getDescription());
            if (partial.getPriority() != null) existing.setPriority(partial.getPriority());
            if (partial.getCategory() != null) existing.setCategory(partial.getCategory());
            if (partial.getDueDate() != null) existing.setDueDate(partial.getDueDate());
            if (partial.getTags() != null && !partial.getTags().isEmpty()) existing.setTags(partial.getTags());
            if (partial.getAssignedTo() != null) existing.setAssignedTo(partial.getAssignedTo());
            existing.setCompleted(partial.isCompleted());
            return repository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public long total() { return repository.count(); }
    public long completed() { return repository.countCompleted(); }
    public long pending() { return repository.countPending(); }
    public long urgentActive() { return repository.countUrgentActive(); }
}
