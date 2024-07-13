package ru.practicum.api.privateApi.userEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.privateApi.userEvents.service.UserEventsServicePrivateApi;
import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.UpdateEventUserRequest;
import ru.practicum.dto.participation.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
public class UserEventsPrivateController {

    private final UserEventsServicePrivateApi userService;

    @GetMapping
    public List<EventShortDto> getEventsAddCurrentUser(@PathVariable long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return userService.findEventsAddCurrentUser(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFulDto postNewEvents(@Positive @PathVariable long userId,
                                     @Valid @RequestBody NewEventDto newEventDto) {
        return userService.saveNewEvents(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFulDto getEventsByIdAndUserId(@Positive @PathVariable long userId,
                                              @Positive @PathVariable long eventId) {
        return userService.findEventsByIdAndUserId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFulDto patchEventByUserIdAndEventId(@Valid @RequestBody UpdateEventUserRequest newEventDto,
                                                    @PathVariable long userId,
                                                    @PathVariable long eventId) {
        return userService.updateEventByUserIdAndEventId(newEventDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserIdAndEventId(@Positive @PathVariable Long userId,
                                                                       @Positive @PathVariable Long eventId) {
        return userService.findRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @RequestBody(required = false) EventRequestStatusUpdateRequest
                                                                                  eventRequestStatusUpdateRequest) {
        return userService.updateRequestsByUserIdAndEventId(eventRequestStatusUpdateRequest, userId, eventId);
    }
}

