package ru.practicum.compilation.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UpdateCompilationRequest {
    Set<Long> events;
    Boolean pinned;
    String title;
}
