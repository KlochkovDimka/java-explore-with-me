package ru.practicum.api.adminApi.events.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.adminApi.events.service.AdminEventsService;
import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.UpdateEventAdminRequest;
import ru.practicum.exceptions.IncorrectlyRequestException;
import ru.practicum.exceptions.ViolationOfRestrictionException;
import ru.practicum.mapper.EventsMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.enams.State;
import ru.practicum.model.enams.StateAction;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventsRepository repository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventFulDto> findAllEventsByParam(List<Long> users,
                                                  List<String> states,
                                                  List<Long> categories,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  int from,
                                                  int size) {
        PageRequest request = PageRequest.of(from / size, size);

        List<Event> events = repository.findByRequestParam(
                users, states, categories, rangeStart, rangeEnd, request
        ).getContent();

        if (events.isEmpty()) {
            throw new RuntimeException("error ListEvents");
        }
        return EventsMapper.convertToListEventsFullDto(events);
    }

    @Override
    public EventFulDto updateEventById(Long eventId, UpdateEventAdminRequest eventRequest) {
        Event event = isEvent(eventId);

        if (eventRequest.getEventDate() != null
                && eventRequest.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new IncorrectlyRequestException("The start date of the event to be " +
                    "modified must be no earlier than one hour from the date of publication");
        }
        if (!event.getState().equals(State.PENDING.toString())) {
            throw new ViolationOfRestrictionException("An event can be published only if it is in " +
                    "the waiting state for publication");
        }

        Event newEvent = updateEventField(event, eventRequest);

        if (eventRequest.getStateAction() != null) {
            newEvent.setState(eventRequest.getStateAction().equals(StateAction.PUBLISH_EVENT.name())
                    ? State.PUBLISHED.name()
                    : State.CANCELED.name());
        }
        return EventsMapper.convertToEventFullDto(
                repository.save(newEvent));
    }

    private Event isEvent(Long eventId) {
        return repository.findById(eventId).orElseThrow(() ->
                new RuntimeException("not found Event"));
    }

    private Event updateEventField(Event event, UpdateEventAdminRequest eventRequest) {
        if (eventRequest.getAnnotation() != null) {
            event.setAnnotation(eventRequest.getAnnotation());
        }
        if (eventRequest.getCategory() != null) {
            event.setCategory(isCategory(eventRequest.getCategory()));
        }
        if (eventRequest.getDescription() != null) {
            event.setDescription(eventRequest.getDescription());
        }
        if (eventRequest.getEventDate() != null) {
            event.setEventDate(eventRequest.getEventDate());
        }
        if (eventRequest.getLocation() != null) {
            event.setLocation(eventRequest.getLocation());
        }
        if (eventRequest.getPaid() != null) {
            event.setPaid(eventRequest.getPaid());
        }
        if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }
        if (eventRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventRequest.getRequestModeration());
        }
        if (eventRequest.getStateAction() != null) {
            event.setState(eventRequest.getStateAction());
        }
        if (eventRequest.getTitle() != null) {
            event.setTitle(eventRequest.getTitle());
        }
        return event;
    }

    private Category isCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new RuntimeException("not found Category"));
    }
}
