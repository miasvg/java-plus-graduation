package ru.practicum.compilation.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.CompilationRequestDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(CompilationRequestDto requestDto);

    CompilationDto getById(Long compId);

    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest request);
}
