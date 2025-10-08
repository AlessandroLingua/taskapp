package com.example.taskapp.web.dto;

import com.example.taskapp.domain.Task;

public final class TaskMapper {
    private TaskMapper() {}

    public static Task toEntity(TaskRequest r) {
        return Task.builder()
                .title(r.getTitle())
                .description(r.getDescription())
                .build();
    }

    public static TaskResponse toResponse(Task t) {
        return TaskResponse.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .build();
    }
}
