package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.CommentEntity;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Collection<CommentEntity> findAllByUserId(Long userId);

    @Query("SELECT c FROM CommentEntity c WHERE " +
            "((?1 IS NULL) OR (c.state IN ?1)) AND " +
            "((?2 IS NULL) OR (c.event.id IN ?2)) AND " +
            "((?3 IS NULL) OR (c.user.id IN ?3)) AND " +
            "((CAST(?4 AS date) IS NULL) OR (c.createdOn >= ?4)) AND " +
            "((CAST(?5 AS date) IS NULL) OR (c.createdOn >= ?5))")
    Page<CommentEntity> findAllCommentsByParam(Collection<String> state,
                                               Long eventId,
                                               Long userId,
                                               LocalDateTime start,
                                               LocalDateTime end, Pageable pageable);

    Collection<CommentEntity> findAllByUserIdAndEventId(Long userId, Long eventId);

    Collection<CommentEntity> findAllByEventId(Long eventId);

    Collection<CommentEntity> findAllByEventIdAndState(Long eventId, String state);
}
