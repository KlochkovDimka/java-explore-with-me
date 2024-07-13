package ru.practicum.api.privateApi.requests.service;

import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> findRequestByUserId(Long userId);

    ParticipationRequestDto createdRequest(Long userId, Long eventId);

    ParticipationRequestDto canselRequest(Long userId, Long requestId);
}
