package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationRequestDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.exeption.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getAll(CompilationRequestDto requestDto) {
        int page = requestDto.getFrom() / requestDto.getSize();
        Pageable pageable = PageRequest.of(page, requestDto.getSize());
        List<Compilation> compilations = compilationRepository.findAllWithFilter(requestDto.getPinned(), pageable);

        return compilations.stream()
                .map(CompilationMapper::mapToDto)
                .toList();
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(Compilation.class.toString(), compId));
        return CompilationMapper.mapToDto(compilation);
    }
}
