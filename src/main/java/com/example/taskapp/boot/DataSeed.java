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
            log.info("Seed dati completato: {} task", repo.count());
        }
    }
}