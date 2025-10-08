package com.example.taskapp.boot;

import com.example.taskapp.domain.Category;
import com.example.taskapp.domain.Task;
import com.example.taskapp.repo.CategoryRepository;
import com.example.taskapp.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
class DataSeed implements CommandLineRunner {

    private final TaskRepository taskRepo;
    private final CategoryRepository catRepo;

    @Override
    public void run(String... args) {
        if (catRepo.count() == 0) {
            Category studio = catRepo.save(Category.builder().name("Studio").description("Formazione").build());
            Category lavoro = catRepo.save(Category.builder().name("Lavoro").description("Progetto").build());

            if (taskRepo.count() == 0) {
                taskRepo.save(Task.builder().title("Imparare Spring Boot").description("API + JPA").category(studio).build());
                taskRepo.save(Task.builder().title("Frontend AngularJS").description("SPA semplice").category(lavoro).build());
            }
            log.info("Seed: {} categories, {} tasks", catRepo.count(), taskRepo.count());
        }
    }
}