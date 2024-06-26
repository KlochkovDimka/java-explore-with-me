package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.service.ServiceHttp;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@Validated
@RequiredArgsConstructor
public class HttpServiceController {

    private final ServiceHttp httpService;

    @PostMapping("/hit")
    public Collection<AppDtoResp> postApp(@RequestBody AppDtoReq dto) {
        return httpService.save(dto);
    }

    @GetMapping("/stats")
    public Collection<AppDtoResp> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) Collection<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return httpService.getStets(start, end, uris, unique);
    }
}
