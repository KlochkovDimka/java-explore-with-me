package ru.practicum.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.participation.ParticipationRequestDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
    private List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

}
