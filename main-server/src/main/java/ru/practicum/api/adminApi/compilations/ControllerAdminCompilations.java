package ru.practicum.api.adminApi.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminApi.compilations.service.CompilationsAdminService;
import ru.practicum.dto.compilations.CompilationDto;
import ru.practicum.dto.compilations.NewCompilationDto;
import ru.practicum.dto.compilations.UpdateCompilationRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class ControllerAdminCompilations {

    private final CompilationsAdminService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postNewCompilations(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return service.saveNewCompilation(newCompilationDto);
    }

    @DeleteMapping("{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@Positive @PathVariable Long compId) {
        service.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto pathCompilationsById(@Positive @PathVariable Long compId,
                                               @Valid @RequestBody UpdateCompilationRequest updateRequest) {
        return service.updateCompilation(compId, updateRequest);
    }
}
