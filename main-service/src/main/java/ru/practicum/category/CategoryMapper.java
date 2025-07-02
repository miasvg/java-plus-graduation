package ru.practicum.category;

import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.CategoryCreateDto;
import ru.practicum.category.model.CategoryDto;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public Category toEntity(CategoryCreateDto dto) {
        return Category.builder().name(dto.getName()).build();
    }

    public void update(Category category, CategoryDto dto) {
        category.setName(dto.getName());
    }
}

