package com.example.taskapp.repo;

import com.example.taskapp.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTitleContainingIgnoreCase(String q, Pageable pageable);
    Page<Task> findByCategory_Id(Long categoryId, Pageable pageable);
    Page<Task> findByCategory_IdAndTitleContainingIgnoreCase(Long categoryId, String q, Pageable pageable);
}