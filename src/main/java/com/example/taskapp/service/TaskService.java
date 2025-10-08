package com.example.taskapp.service;

import com.example.taskapp.web.dto.TaskRequest;
import com.example.taskapp.web.dto.TaskResponse;
import java.util.List;

public interface TaskService {
    List<TaskResponse> findAll();
    TaskResponse findById(Long id);
    TaskResponse create(TaskRequest request);
    TaskResponse update(Long id, TaskRequest request);
    void delete(Long id);
}