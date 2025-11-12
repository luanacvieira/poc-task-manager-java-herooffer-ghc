package com.example.taskmanager.repository;

import com.example.taskmanager.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select count(t) from Task t where t.completed = true")
    long countCompleted();

    @Query("select count(t) from Task t where t.completed = false")
    long countPending();

    @Query("select count(t) from Task t where t.priority = 'URGENT' and t.completed = false")
    long countUrgentActive();
}
