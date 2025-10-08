package com.example.taskapp.service;

import com.example.taskapp.domain.Category;
import com.example.taskapp.domain.Task;
import com.example.taskapp.repo.CategoryRepository;
import com.example.taskapp.repo.TaskRepository;
import com.example.taskapp.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final CategoryRepository catRepo;

    @Override @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskRepo.findAll().stream().map(TaskMapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task t = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " non trovato"));
        return TaskMapper.toResponse(t);
    }

    @Override
    public TaskResponse create(TaskRequest r) {
        Task t = Task.builder()
                .title(r.getTitle())
                .description(r.getDescription())
                .build();
        if (r.getCategoryId() != null) {
            Category c = catRepo.findById(r.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category " + r.getCategoryId() + " non trovata"));
            t.setCategory(c);
        }
        return TaskMapper.toResponse(taskRepo.save(t));
    }

    @Override
    public TaskResponse update(Long id, TaskRequest r) {
        Task t = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " non trovato"));
        t.setTitle(r.getTitle());
        t.setDescription(r.getDescription());
        if (r.getCategoryId() != null) {
            Category c = catRepo.findById(r.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category " + r.getCategoryId() + " non trovata"));
            t.setCategory(c);
        } else {
            t.setCategory(null);
        }
        return TaskMapper.toResponse(taskRepo.save(t));
    }

    @Override
    public void delete(Long id) {
        if (!taskRepo.existsById(id)) throw new NotFoundException("Task " + id + " non trovato");
        taskRepo.deleteById(id);
    }
}