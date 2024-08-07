package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventsRepository extends JpaRepository<Event, Long> {

    Page<Event> findByInitiator(User user, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "((:users IS NULL) OR (e.initiator.id IN :users)) AND " +
            "((:states IS NULL) OR (e.state IN :states)) AND " +
            "((:categories IS NULL) OR (e.category.id IN :categories)) " +
            "AND ((CAST(:rangeStart AS date) IS NULL) OR (e.eventDate >= :rangeStart)) " +
            "AND ((CAST(:rangeEnd AS date) IS NULL) OR (e.eventDate <= :rangeEnd))")
    Page<Event> findByRequestParam(@Param("users") List<Long> users,
                                   @Param("states") List<String> states,
                                   @Param("categories") List<Long> categories,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND ((:categories IS NULL) OR (e.category.id IN :categories)) " +
            "AND ((:paid IS NULL) OR (e.paid = :paid)) " +
            "AND ((CAST(:start AS date) IS NULL) OR (e.eventDate >= :start)) " +
            "AND ((CAST(:end AS date) IS NULL) OR (e.eventDate <= :end)) " +
            "AND (:onlyAvailable = false OR e.participantLimit > e.confirmedRequests OR e.participantLimit = 0)")
    Page<Event> findAllByRequestParam(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("onlyAvailable") Boolean onlyAvailable,
            Pageable pageable);
}
