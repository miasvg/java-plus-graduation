package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid NewCompilationDto dto) {
        log.info("Создание подборки {} для событий с ID: {}", dto.getTitle(), dto.getEvents());
        return compilationService.create(dto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping("/{compId}")
    public void delete(@PathVariable @Positive Long compId) {
        log.info("Удаление подборки событий с ID: {}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping
    @RequestMapping("/{compId}")
    public CompilationDto update(@PathVariable @Positive Long compId,
                                 UpdateCompilationRequest request) {
        log.info("Обновление подборки событий с ID: {}", compId);
        return compilationService.update(compId, request);
    }
}
