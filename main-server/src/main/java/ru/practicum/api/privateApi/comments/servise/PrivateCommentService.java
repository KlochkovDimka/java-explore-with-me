package ru.practicum.api.privateApi.comments.servise;

import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.dto.comments.UpdateCommentDto;

import java.util.Collection;

public interface PrivateCommentService {

    CommentDto saveComment(NewCommentDto newCommentDto, Long userId, Long eventId);

    CommentDto findCommentById(Long comId);

    Collection<CommentDto> findCommentsByUserId(Long userId, Long eventId);

    CommentDto updateCommentById(Long userId, Long comId, UpdateCommentDto updateCommentDto);

    void deleteCommentById(Long userId, Long comId);
}
