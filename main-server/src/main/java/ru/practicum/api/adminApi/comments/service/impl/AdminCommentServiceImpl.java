package ru.practicum.api.adminApi.comments.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.api.adminApi.comments.service.AdminCommentService;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.CommentParam;
import ru.practicum.exceptions.IncorrectlyRequestException;
import ru.practicum.exceptions.NotFoundEntity;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.CommentEntity;
import ru.practicum.model.enams.State;
import ru.practicum.repository.CommentRepository;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;


    @Override
    public Collection<CommentDto> findAllCommentByParam(CommentParam commentParam, PageRequest pageRequest) {

        Collection<CommentEntity> comments = commentRepository.findAllCommentsByParam(commentParam.getState(),
                commentParam.getComId(),
                commentParam.getUserId(),
                commentParam.getStart(),
                commentParam.getEnd(),
                pageRequest).getContent();

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }
        return CommentMapper.toListCommentDto(comments);
    }

    @Override
    public CommentDto commentUpdateStatus(Long comId, String state) {
        CommentEntity comment = isComment(comId);
        if (!comment.getState().equals(State.PENDING.name())) {
            throw new IncorrectlyRequestException(String.format("Comment whit id=%d has not state PENDING", comId));
        }
        comment.setState(state.equals(State.PUBLISHED.name())
                ? State.PUBLISHED.name()
                : State.CANCELED.name());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private CommentEntity isComment(Long comId) {
        return commentRepository.findById(comId).orElseThrow(() ->
                new NotFoundEntity(String.format("Comment id=%d", comId)));
    }


}
