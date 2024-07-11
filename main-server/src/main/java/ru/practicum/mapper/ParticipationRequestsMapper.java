package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.participation.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationRequestsMapper {

    public static ParticipationRequestDto convertToParticipationRequestDto(ParticipationRequest requests) {
        return ParticipationRequestDto.builder()
                .created(requests.getCreated())
                .event(requests.getEvent().getId())
                .id(requests.getId())
                .requester(requests.getRequester().getId())
                .status(requests.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> convertToListParticipationRequestDto(
            List<ParticipationRequest> participationRequests) {
        return participationRequests.stream()
                .map(ParticipationRequestsMapper::convertToParticipationRequestDto)
                .collect(Collectors.toList());
    }
}
