package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.service.ServiceHttp;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@Validated
@RequiredArgsConstructor
@Slf4j
public class HttpServiceController {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final ServiceHttp httpService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public Collection<AppDtoResp> postApp(@RequestBody AppDtoReq dto) {
        return httpService.save(dto);
    }

    @GetMapping("/stats")
    public Collection<AppDtoResp> getStats(@RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                           @RequestParam(required = false) Collection<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return httpService.getStets(start, end, uris, unique);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
