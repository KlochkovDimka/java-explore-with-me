package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class CompilationMapper {
    public static CompilationDto convertToCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(EventsMapper.convertToListEventShortDto(compilation.getEvents()))
                .build();
    }

    public static List<CompilationDto> convertToListCompilationDto(List<Compilation> compilations) {
        return compilations.stream()
                .map(CompilationMapper::convertToCompilationDto)
                .collect(Collectors.toList());
    }

    public static Compilation convertToCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

}
