package com.example.taskapp.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskRequest {
    @NotBlank(message = "title è obbligatorio")
    private String title;
    private String description;
    private Long categoryId; // opzionale
}