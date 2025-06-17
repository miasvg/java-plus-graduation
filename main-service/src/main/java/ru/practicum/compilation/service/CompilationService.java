package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationRequestDto;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(CompilationRequestDto requestDto);

    CompilationDto getById(Long compId);
}
