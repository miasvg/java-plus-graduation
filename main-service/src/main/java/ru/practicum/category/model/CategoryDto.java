package ru.practicum.category.model;

//потом перенесем в отдельный модуль все дто

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
}
