package ru.practicum.api.publicApi.events.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.AppClient;
import ru.practicum.api.publicApi.events.service.EventsService;
import ru.practicum.appDto.AppDtoReq;
import ru.practicum.appDto.AppDtoResp;
import ru.practicum.dto.comments.ShortCommentDto;
import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.exceptions.IncorrectlyRequestException;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.EventsMapper;
import ru.practicum.model.Event;
import ru.practicum.model.enams.State;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventsRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventsServiceImpl implements EventsService {

    private final EventsRepository eventsRepository;
    private final CommentRepository commentRepository;

    private final AppClient client;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findAllEventByRequestParam(String text,
                                                          List<Long> categories,
                                                          Boolean paid,
                                                          LocalDateTime rangeStart,
                                                          LocalDateTime rangeEnd,
                                                          Boolean onlyAvailable,
                                                          String sort,
                                                          Integer from,
                                                          Integer size,
                                                          HttpServletRequest request) {

        Sort sorting = Sort.unsorted();
        if ("EVENT_DATE".equals(sort)) {
            sorting = Sort.by("eventDate");
        } else if ("VIEWS".equals(sort)) {
            sorting = Sort.by("views");
        }

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new IncorrectlyRequestException("Error time");
            }
        }
        PageRequest pageRequest = PageRequest.of(from / size, size, sorting);

        Page<Event> listPage = eventsRepository.findAllByRequestParam(text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                pageRequest);

        if (listPage == null || listPage.isEmpty()) {
            return List.of();
        }

        saveView(request);
        List<EventShortDto> eventShortDtos = EventsMapper.convertToListEventShortDto(listPage.getContent());
        eventShortDtos
                .forEach(eventShortDto -> eventShortDto.setComments(getCommentDto(eventShortDto.getId())));
        return eventShortDtos;
    }

    @Override
    @Transactional
    public EventFulDto findEventById(Long eventId, HttpServletRequest request) {
        saveView(request);
        Event event = isEvent(eventId);
        event.setViews(countViews(request));

        Event updateEvent = eventsRepository.save(event);
        if (!Objects.equals(updateEvent.getState(), State.PUBLISHED.name())) {
            throw new NotFoundEntity(String.format("Event id=%d state=PUBLISHED ", eventId));
        }
        EventFulDto eventFulDto = EventsMapper.convertToEventFullDto(event);
        eventFulDto.setComments(getCommentDto(eventFulDto.getId()));
        return eventFulDto;
    }

    private void saveView(HttpServletRequest request) {
        AppDtoReq appDtoReq = AppDtoReq.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timeStamp(LocalDateTime.now())
                .build();

        client.createApp(appDtoReq);
    }

    private Event isEvent(Long eventId) {
        return eventsRepository.findById(eventId).orElseThrow(() ->
                new RuntimeException("not found event"));
    }

    private long countViews(HttpServletRequest request) {
        List<AppDtoResp> stats = client.getAllApp(
                LocalDateTime.now().minusYears(5),
                LocalDateTime.now().plusYears(5),
                List.of(request.getRequestURI()),
                true);
        return stats.stream()
                .findFirst()
                .map(AppDtoResp::getHits)
                .orElse(0L);
    }

    private Collection<ShortCommentDto> getCommentDto(Long eventId) {
        return CommentMapper.toListShortCommentDto(commentRepository.findAllByEventIdAndState(eventId,
                State.PUBLISHED.name()));
    }
}
