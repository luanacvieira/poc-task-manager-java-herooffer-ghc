package com.example.taskmanager;

import com.example.taskmanager.domain.Category;
import com.example.taskmanager.domain.Priority;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskServiceTests {

    @Autowired
    TaskService service;

    @Test
    void createAndCount() {
        Task t = new Task();
        t.setTitle("Teste");
        t.setPriority(Priority.HIGH);
        t.setCategory(Category.WORK);
        t.setUserId("user1");
        t.setDueDate(LocalDate.of(2025,11,13));
        service.create(t);
        assertThat(service.total()).isEqualTo(1);
        assertThat(service.pending()).isEqualTo(1);
        assertThat(service.completed()).isZero();
    }
}
