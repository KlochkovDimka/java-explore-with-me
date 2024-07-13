package ru.practicum.api.privateApi.userEvents.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.privateApi.userEvents.service.UserEventsServicePrivateApi;
import ru.practicum.dto.events.EventFulDto;
import ru.practicum.dto.events.EventRequestStatusUpdateRequest;
import ru.practicum.dto.events.EventRequestStatusUpdateResult;
import ru.practicum.dto.events.EventShortDto;
import ru.practicum.dto.events.NewEventDto;
import ru.practicum.dto.events.UpdateEventUserRequest;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.exceptions.IncorrectlyRequestException;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.exceptions.ViolationOfRestrictionException;
import ru.practicum.mapper.EventsMapper;
import ru.practicum.mapper.ParticipationRequestsMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enams.State;
import ru.practicum.model.enams.Status;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.ParticipationRequestsRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.model.enams.StateAction.CANCEL_REVIEW;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventsServicePrivateApiImpl implements UserEventsServicePrivateApi {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestsRepository participationRequestsRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> findEventsAddCurrentUser(Long userId, int from, int size) {

        User user = isUser(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        Page<Event> eventPage = eventsRepository.findByInitiator(user, pageRequest);

        log.info("UserEventsServicePrivateApi-findEventsAddCurrentUser-events={}", eventPage.getContent());

        if (eventPage.getContent().isEmpty()) {
            return List.of();
        }
        return EventsMapper.convertToListEventShortDto(eventPage.getContent());
    }

    @Override
    @Transactional
    public EventFulDto saveNewEvents(Long userId, NewEventDto newEventDto) {

        User user = isUser(userId);
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyRequestException("Error date");
        }
        Event event = EventsMapper.convertToEvent(newEventDto, isCategory(newEventDto.getCategory()));
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(user);
        event.setState(State.PENDING.name());
        event.setConfirmedRequests(0L);
        event.setPublishedOn(LocalDateTime.now());
        event.setViews(0L);

        return EventsMapper.convertToEventFullDto(eventsRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFulDto findEventsByIdAndUserId(Long userId, Long eventsId) {
        Event event = isEvent(eventsId);
        User initiator = event.getInitiator();

        if (!Objects.equals(initiator.getId(), userId)) {
            throw new ViolationOfRestrictionException("Error initiator");
        }
        return EventsMapper.convertToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFulDto updateEventByUserIdAndEventId(UpdateEventUserRequest newEventDto, Long userId, Long eventId) {
        isUser(userId);
        Event oldEvent = isEvent(eventId);
        if (oldEvent.getState().equals(State.PUBLISHED.name())) {
            throw new ViolationOfRestrictionException("Error state");
        }
        Event newEvent = updateFieldToEvent(oldEvent, newEventDto);

        if (newEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new IncorrectlyRequestException("Error date");
        }

        return EventsMapper.convertToEventFullDto(eventsRepository.save(newEvent));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findRequestsByUserIdAndEventId(Long userId, Long eventId) {
        isUser(userId);
        isEvent(eventId);
        List<ParticipationRequest> requests = participationRequestsRepository
                .findAllByEventIdAndInitiatorId(eventId, userId);
        if (requests.isEmpty()) {
            return List.of();
        }
        return ParticipationRequestsMapper.convertToListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsByUserIdAndEventId(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId) {

        // Проверяю пользователя
        isUser(userId);

        // Проверяю и получаю событие
        Event event = isEvent(eventId);
        // получаю модели запросы по переданному списку
        if (eventRequestStatusUpdateRequest == null) {
            throw new ViolationOfRestrictionException("Request body null");
        }
        List<ParticipationRequest> participationRequests = findListRequest(eventRequestStatusUpdateRequest
                .getRequestIds());

        // Создаю класс результата
        EventRequestStatusUpdateResult updateResult = new EventRequestStatusUpdateResult();

        if (eventRequestStatusUpdateRequest.getStatus().equals(Status.REJECTED.name())) {
            participationRequests.stream()
                    .peek(request -> request.setStatus(Status.REJECTED.name()))
                    .map(participationRequestsRepository::save)
                    .forEach(request -> updateResult.getRejectedRequests().add(ParticipationRequestsMapper
                            .convertToParticipationRequestDto(request)));
            return updateResult;
        }
        // Если модерация не требуется или лимитов нет
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {

            updateResult.setConfirmedRequests(participationRequests.stream()
                    .peek(request -> request.setStatus(Status.CONFIRMED.name()))
                    .peek(participationRequestsRepository::save)
                    .map(ParticipationRequestsMapper::convertToParticipationRequestDto)
                    .collect(Collectors.toList()));
            return updateResult;
        } else {
            return addRequest(event, participationRequests);
        }
    }

    private EventRequestStatusUpdateResult addRequest(Event event, List<ParticipationRequest> participationRequests) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        long limit = event.getParticipantLimit();
        long confirmedRequests = event.getConfirmedRequests();

        for (ParticipationRequest requests : participationRequests) {
            if (limit > confirmedRequests) {
                requests.setStatus(Status.CONFIRMED.toString());
                ParticipationRequest newRequests = participationRequestsRepository.save(requests);
                result.getConfirmedRequests()
                        .add(ParticipationRequestsMapper.convertToParticipationRequestDto(newRequests));
                confirmedRequests++;
            } else {
                requests.setStatus(Status.REJECTED.toString());
                ParticipationRequest newRequest = participationRequestsRepository.save(requests);
                result.getRejectedRequests()
                        .add(ParticipationRequestsMapper.convertToParticipationRequestDto(newRequest));
            }
        }
        return result;
    }

    private List<ParticipationRequest> findListRequest(List<Long> participationRequestId) {
        return participationRequestId.stream()
                .map(participationRequestsRepository::findById)
                .map(participationRequests1 ->
                        participationRequests1.orElseThrow(() ->
                                new NotFoundEntity("Participation Request not found")
                        )
                )
                .peek(participationRequest1 -> {
                            if (!participationRequest1.getStatus().equals(Status.PENDING.name())) {
                                throw new ViolationOfRestrictionException("Error status");
                            }
                        }
                )
                .filter(participationRequest1 ->
                        participationRequest1.getStatus().equals(Status.PENDING.toString()))
                .collect(Collectors.toList());
    }


    private User isUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundEntity(String.format("User with id=%d", userId)));
    }

    private Event isEvent(Long eventId) {
        return eventsRepository.findById(eventId).orElseThrow(() ->
                new NotFoundEntity(String.format("Event with id=%d", eventId)));
    }

    private Category isCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundEntity(String.format("Category with id=%d", catId)));
    }

    private Event updateFieldToEvent(Event event, UpdateEventUserRequest eventDto) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }
        if (eventDto.getCategory() != null) {
            event.setCategory(isCategory(eventDto.getCategory()));
        }
        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }
        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }
        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }
        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }
        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }
        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }
        if (Objects.equals(eventDto.getStateAction(), CANCEL_REVIEW.name())) {
            event.setState(State.CANCELED.name());
        } else {
            event.setState(State.PENDING.name());
        }
        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }
        return event;
    }
}
