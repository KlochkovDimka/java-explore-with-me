package ru.practicum.api.privateApi.requests.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.privateApi.requests.service.RequestService;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.exceptions.ViolationOfRestrictionException;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.ParticipationRequestsMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enams.State;
import ru.practicum.model.enams.Status;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.ParticipationRequestsRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final ParticipationRequestsRepository requestsRepository;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> findRequestByUserId(Long userId) {
        User user = isUser(userId);
        List<ParticipationRequest> requests = requestsRepository.findAllByRequesterId(user.getId());
        if (requests.isEmpty()) {
            throw new RuntimeException("not found request");
        }
        return ParticipationRequestsMapper.convertToListParticipationRequestDto(requests);
    }

    @Override
    @Transactional
    public ParticipationRequestDto createdRequest(Long userId, Long eventId) {

        User user = isUser(userId);
        Event event = isEvent(eventId);

        if (requestsRepository.findByEventIdAndRequesterId(event.getId(), user.getId()).isPresent()) {
            throw new ViolationOfRestrictionException("You cannot add a repeat request");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ViolationOfRestrictionException("The initiator of the event " +
                    "cannot add a request to participate in his event");
        }
        if (!Objects.equals(event.getState(), State.PUBLISHED.toString())) {
            throw new ViolationOfRestrictionException("You cannot participate in an " +
                    "unpublished event");
        }
        if (event.getConfirmedRequests() != 0 && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ViolationOfRestrictionException("The limit of participation " +
                    "requests has been reached");
        }

        event.setConfirmedRequests(event.getConfirmedRequests() + 1L);
        eventsRepository.save(event);

        ParticipationRequest requests = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .build();

        requests.setStatus(!event.getRequestModeration() || event.getParticipantLimit() == 0
                ? Status.CONFIRMED.name() : Status.PENDING.name());

        ParticipationRequest request = requestsRepository.save(requests);
        return ParticipationRequestsMapper.convertToParticipationRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto canselRequest(Long userId, Long requestId) {
        ParticipationRequest requests = isRequest(requestId);
        if (!requests.getRequester().getId().equals(userId)) {
            throw new RuntimeException("");
        }
        requests.setStatus(State.CANCELED.name());
        return ParticipationRequestsMapper.convertToParticipationRequestDto(
                requestsRepository.save(requests));
    }

    private User isUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundEntity(String.format("User with id=%d", userId)));
    }

    private Event isEvent(Long eventId) {
        return eventsRepository.findById(eventId).orElseThrow(() ->
                new NotFoundEntity(String.format("Event with id=%d", eventId)));
    }

    private ParticipationRequest isRequest(Long requestId) {
        return requestsRepository.findById(requestId).orElseThrow(() ->
                new RuntimeException("request not found"));
    }
}
