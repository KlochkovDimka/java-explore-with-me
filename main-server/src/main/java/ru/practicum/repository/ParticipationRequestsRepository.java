package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestsRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long userId);

    @Query("SELECT pr FROM ParticipationRequest pr " +
            "JOIN pr.event ev " +
            "JOIN ev.initiator i " +
            "WHERE ev.id=?1 AND i.id=?2")
    List<ParticipationRequest> findAllByEventIdAndInitiatorId(Long eventId, Long userId);

    List<ParticipationRequest> findAllByRequesterId(Long userId);
}
