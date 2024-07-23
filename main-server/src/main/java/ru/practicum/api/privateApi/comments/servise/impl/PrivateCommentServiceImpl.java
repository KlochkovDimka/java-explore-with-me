package ru.practicum.api.privateApi.comments.servise.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.api.privateApi.comments.servise.PrivateCommentService;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.dto.comments.UpdateCommentDto;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.exceptions.ViolationOfRestrictionException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.CommentEntity;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.enams.State;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventsRepository;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    @Override
    @Transactional
    public CommentDto saveComment(NewCommentDto newCommentDto, Long userId, Long eventId) {

        User user = isUser(userId);
        Event event = isEvent(eventId);

        CommentEntity comment = CommentMapper.toCommentEntity(newCommentDto);

        comment.setUser(user);
        comment.setEvent(event);

        if (event.getInitiator().getId().equals(userId)) {
            comment.setState(State.PUBLISHED.name());
        } else {
            comment.setState(event.getRequestModeration()
                    ? State.PENDING.name()
                    : State.PUBLISHED.name());
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto findCommentById(Long comId) {
        CommentEntity comment = isComment(comId);

        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CommentDto> findCommentsByUserId(Long userId, Long eventId) {

        Collection<CommentEntity> comments = getCommentsByUserIdAndOrEventId(userId, eventId);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        return CommentMapper.toListCommentDto(comments);
    }

    @Override
    @Transactional
    public CommentDto updateCommentById(Long userId, Long comId, UpdateCommentDto updateCommentDto) {
        isUser(userId);
        CommentEntity comment = isComment(comId);

        isCommentByUser(comment, userId);

        return CommentMapper.toCommentDto(updateFieldComment(comment, updateCommentDto));
    }

    @Override
    @Transactional
    public void deleteCommentById(Long userId, Long comId) {
        isUser(userId);
        CommentEntity comment = isComment(comId);

        isCommentByUser(comment, userId);

        commentRepository.deleteById(comId);
    }

    private User isUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundEntity("User id=" + userId));
    }

    private Event isEvent(Long eventId) {
        return eventsRepository.findById(eventId).orElseThrow(() ->
                new NotFoundEntity("Event id=" + eventId));
    }

    private CommentEntity isComment(Long comId) {
        return commentRepository.findById(comId).orElseThrow(() ->
                new NotFoundEntity("Comment id=" + comId));
    }

    private void isCommentByUser(CommentEntity comment, Long userId) {
        Event event = isEvent(comment.getEvent().getId());
        if (!comment.getUser().getId().equals(userId)
                && !event.getInitiator().getId().equals(userId)) {
            throw new ViolationOfRestrictionException("Error user");
        }
    }

    private CommentEntity updateFieldComment(CommentEntity oldEntity, UpdateCommentDto updateEntity) {
        oldEntity.setValue(updateEntity.getValue());
        return oldEntity;
    }

    private Collection<CommentEntity> getCommentsByUserIdAndOrEventId(Long userId, Long eventId) {
        if (userId != null && eventId != null) {
            isUser(userId);
            isEvent(eventId);
            return commentRepository.findAllByUserIdAndEventId(userId, eventId);
        }
        if (eventId == null && userId == null) {
            return commentRepository.findAll();
        }
        if (eventId != null) {
            isEvent(eventId);
            return commentRepository.findAllByEventId(eventId);
        }
        isUser(userId);
        return commentRepository.findAllByUserId(userId);
    }
}
