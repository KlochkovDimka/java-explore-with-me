package ru.practicum.api.adminApi.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminApi.events.service.AdminEventsService;
import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.UpdateEventAdminRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
public class ControllerAdminEvents {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final AdminEventsService adminEventsService;

    @GetMapping
    public List<EventFulDto> getEvents(@RequestParam(required = false) List<Long> users,
                                       @RequestParam(required = false) List<String> states,
                                       @RequestParam(required = false) List<Long> categories,
                                       @RequestParam(required = false)
                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                       @RequestParam(required = false)
                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return adminEventsService
                .findAllEventsByParam(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("{eventId}")
    public EventFulDto pathEventsById(@PathVariable Long eventId,
                                      @Valid @RequestBody UpdateEventAdminRequest eventRequest) {
        return adminEventsService.updateEventById(eventId, eventRequest);
    }
}
