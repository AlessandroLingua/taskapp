package com.example.taskapp.service;

import com.example.taskapp.domain.Task;
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

    private final TaskRepository repo;

    @Override @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return repo.findAll().stream().map(TaskMapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task t = repo.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " non trovato"));
        return TaskMapper.toResponse(t);
    }

    @Override
    public TaskResponse create(TaskRequest request) {
        Task saved = repo.save(TaskMapper.toEntity(request));
        return TaskMapper.toResponse(saved);
    }

    @Override
    public TaskResponse update(Long id, TaskRequest r) {
        Task t = repo.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " non trovato"));
        t.setTitle(r.getTitle());
        t.setDescription(r.getDescription());
        return TaskMapper.toResponse(t);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Task " + id + " non trovato");
        repo.deleteById(id);
    }
}
