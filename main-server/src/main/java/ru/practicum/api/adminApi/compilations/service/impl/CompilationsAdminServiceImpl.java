package ru.practicum.api.adminApi.compilations.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.adminApi.compilations.service.CompilationsAdminService;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.compilations.UpdateCompilationRequest;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventsRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationsAdminServiceImpl implements CompilationsAdminService {

    private final CompilationRepository repository;
    private final EventsRepository eventsRepository;

    @Override
    @Transactional
    public CompilationDto saveNewCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.convertToCompilation(newCompilationDto);
        compilation.setEvents(isListEvents(newCompilationDto.getEvents()));
        return CompilationMapper.convertToCompilationDto(repository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        repository.deleteById(compilationId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long complId, UpdateCompilationRequest updateRequest) {
        Compilation compilation = isCompilation(complId);

        List<Event> eventsToUpdate = new ArrayList<>();

        if (updateRequest.getEvents() != null) {
            eventsToUpdate.addAll(eventsRepository.findAllById(updateRequest.getEvents()));
        }

        compilation.setEvents(eventsToUpdate);

        Compilation compilation1 = repository.save(compilation);

        return CompilationMapper.convertToCompilationDto(compilation1);
    }

    private Compilation isCompilation(Long complId) {
        return repository.findById(complId).orElseThrow(() ->
                new NotFoundEntity(String.format("Compilation whit id=%d", complId)));
    }

    private List<Event> isListEvents(List<Long> eventsId) {
        if (eventsId == null) {
            return List.of();
        }
        return eventsRepository.findAllById(eventsId);
    }
}
