package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    List<Long> events;
    Boolean pinned;
    @NotBlank(message = "Необходимо указать заголовок")
    String title;
}
