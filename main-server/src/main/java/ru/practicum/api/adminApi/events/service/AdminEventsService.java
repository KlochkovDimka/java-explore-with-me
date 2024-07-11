package ru.practicum.api.adminApi.events.service;

import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.UpdateEventAdminRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventsService {

    List<EventFulDto> findAllEventsByParam(List<Long> users,
                                           List<String> states,
                                           List<Long> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           int from,
                                           int size);

    EventFulDto updateEventById(Long eventId, UpdateEventAdminRequest eventRequest);
}
