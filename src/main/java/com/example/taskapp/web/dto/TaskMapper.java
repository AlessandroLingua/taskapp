package com.example.taskapp.web.dto;

import com.example.taskapp.domain.Task;

public final class TaskMapper {
    private TaskMapper() {}

    public static TaskResponse toResponse(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .categoryId(t.getCategory() != null ? t.getCategory().getId() : null)
                .categoryName(t.getCategory() != null ? t.getCategory().getName() : null)
                .build();
    }
}