package ru.practicum.api.privateApi.userEvents.service;

import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.UpdateEventUserRequest;
import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface UserEventsServicePrivateApi {

    List<EventShortDto> findEventsAddCurrentUser(Long userId, int from, int size);

    EventFulDto saveNewEvents(Long userId, NewEventDto newEventDto);

    EventFulDto findEventsByIdAndUserId(Long userId, Long eventsId);

    EventFulDto updateEventByUserIdAndEventId(UpdateEventUserRequest newEventDto, Long userId, Long eventId);

    List<ParticipationRequestDto> findRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId);
}
