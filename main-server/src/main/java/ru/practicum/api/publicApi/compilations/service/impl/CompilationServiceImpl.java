package ru.practicum.api.publicApi.compilations.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.api.publicApi.compilations.service.CompilationService;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageable = PageRequest.of(from, size);
        Page<Compilation> compilations = compilationRepository.findAll(pageable);
        if (compilations.getContent().isEmpty()) {
            return List.of();
        }
        return CompilationMapper.convertToListCompilationDto(compilations.getContent());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundEntity(String.format("Compilation id=%d", compId)));
        return CompilationMapper.convertToCompilationDto(compilation);
    }
}
