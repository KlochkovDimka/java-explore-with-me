package ru.practicum.api.publicApi.events.service;

import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventsService {

    List<EventShortDto> findAllEventByRequestParam(String text,
                                                   List<Long> categories,
                                                   Boolean paid,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd,
                                                   Boolean onlyAvailable,
                                                   String sort,
                                                   Integer from,
                                                   Integer size,
                                                   HttpServletRequest request);

    EventFulDto findEventById(Long eventId, HttpServletRequest request);
}
