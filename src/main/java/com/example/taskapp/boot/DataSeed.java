package com.example.taskapp.boot;

import com.example.taskapp.domain.Task;
import com.example.taskapp.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DataSeed implements CommandLineRunner {

    private final TaskRepository repo;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            Task t1 = new Task();
            t1.setTitle("Spring Boot");
            t1.setDescription("API + JPA");

            Task t2 = new Task();
            t2.setTitle("Frontend AngularJS");
            t2.setDescription("SPA semplice");

            repo.save(t1);
            repo.save(t2);

            log.info("Seed dati completato: {} task", repo.count());
        }
    }
}