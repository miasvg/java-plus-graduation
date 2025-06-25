package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NewCompilationDto {
    List<Long> events;
    Boolean pinned = false;
    @NotNull(message = "Необходимо указать заголовок")
    String title;
}

