package ru.practicum.category.model;

//потом перенесем в отдельный модуль все дто
// в спеке это NewCategoryDto
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDto {
    private String name;
}
