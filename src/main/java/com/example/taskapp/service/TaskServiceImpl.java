package com.example.taskapp.service;

import com.example.taskapp.domain.Category;
import com.example.taskapp.domain.Task;
import com.example.taskapp.repo.CategoryRepository;
import com.example.taskapp.repo.TaskRepository;
import com.example.taskapp.web.dto.PagedResponse;
import com.example.taskapp.web.dto.TaskMapper;
import com.example.taskapp.web.dto.TaskRequest;
import com.example.taskapp.web.dto.TaskResponse;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;
    private final CategoryRepository catRepo;
    private final MeterRegistry meterRegistry;

    private Counter createdCounter() { return meterRegistry.counter("tasks.created.count"); }
    private Counter deletedCounter() { return meterRegistry.counter("tasks.deleted.count"); }

    @Override @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskRepo.findAll().stream().map(TaskMapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true) @Timed(value = "tasks.findAllPaged.timer")
    public PagedResponse<TaskResponse> findAll(Pageable pageable, String q, Long categoryId) {
        Page<Task> page;
        if (categoryId != null && q != null && !q.isBlank()) {
            page = taskRepo.findByCategory_IdAndTitleContainingIgnoreCase(categoryId, q, pageable);
        } else if (categoryId != null) {
            page = taskRepo.findByCategory_Id(categoryId, pageable);
        } else if (q != null && !q.isBlank()) {
            page = taskRepo.findByTitleContainingIgnoreCase(q, pageable);
        } else {
            page = taskRepo.findAll(pageable);
        }
        var content = page.getContent().stream().map(TaskMapper::toResponse).toList();
        return PagedResponse.<TaskResponse>builder()
                .content(content)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Override @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task t = taskRepo.findById(id).orElseThrow(() -> new NotFoundException("Task " + id + " non trovato"));
        return TaskMapper.toResponse(t);
    }

    @Override @Timed(value = "tasks.create.timer")
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
        Task saved = taskRepo.save(t);
        createdCounter().increment();
        return TaskMapper.toResponse(saved);
    }

    @Override @Timed(value = "tasks.update.timer")
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
        Task saved = taskRepo.save(t);
        return TaskMapper.toResponse(saved);
    }

    @Override @Timed(value = "tasks.delete.timer")
    public void delete(Long id) {
        if (!taskRepo.existsById(id)) throw new NotFoundException("Task " + id + " non trovato");
        taskRepo.deleteById(id);
        deletedCounter().increment();
    }
}