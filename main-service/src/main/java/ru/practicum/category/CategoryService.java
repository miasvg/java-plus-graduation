package ru.practicum.category;

import ru.practicum.category.model.CategoryCreateDto;
import ru.practicum.category.model.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryCreateDto dto);

    CategoryDto update(Long catId, CategoryDto dto);

    void delete(Long catId);

    List<CategoryDto> findAll(int from, int size);

    CategoryDto findById(Long id);

}

