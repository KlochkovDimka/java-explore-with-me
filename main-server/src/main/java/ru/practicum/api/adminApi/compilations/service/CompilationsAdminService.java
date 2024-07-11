package ru.practicum.api.adminApi.compilations.service;

import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.compilations.UpdateCompilationRequest;

public interface CompilationsAdminService {

    CompilationDto saveNewCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compilationId);

    CompilationDto updateCompilation(Long complId, UpdateCompilationRequest updateRequest);
}
