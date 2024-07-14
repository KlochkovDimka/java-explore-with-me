package ru.practicum.api.adminApi.comments.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.CommentParam;

import java.util.Collection;

public interface AdminCommentService {

    Collection<CommentDto> findAllCommentByParam(CommentParam commentParam, PageRequest pageRequest);

    CommentDto commentUpdateStatus(Long comId, String state);
}
