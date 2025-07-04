package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    Set<Long> events = new HashSet<>();
    Boolean pinned = false;
    @NotBlank(message = "Необходимо указать заголовок")
    String title;
}