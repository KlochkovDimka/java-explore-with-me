package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.dto.comments.ShortCommentDto;
import ru.practicum.model.CommentEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentEntity toCommentEntity(NewCommentDto newCommentDto) {
        return CommentEntity.builder()
                .value(newCommentDto.getValue())
                .createdOn(LocalDateTime.now())
                .build();
    }

    public static CommentDto toCommentDto(CommentEntity entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .event(EventsMapper.convertToEventShortDto(entity.getEvent()))
                .user(UserMapper.convertToUserDto(entity.getUser()))
                .value(entity.getValue())
                .state(entity.getState())
                .createOn(entity.getCreatedOn())
                .build();
    }

    public static Collection<CommentDto> toListCommentDto(Collection<CommentEntity> commentEntities) {
        return commentEntities.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static ShortCommentDto toShortCommentDto(CommentEntity commentEntity) {
        return ShortCommentDto.builder()
                .user(UserMapper.convertToUserDto(commentEntity.getUser()))
                .value(commentEntity.getValue())
                .createdOn(commentEntity.getCreatedOn())
                .build();
    }

    public static Collection<ShortCommentDto> toListShortCommentDto(Collection<CommentEntity> entities) {
        return entities.stream()
                .map(CommentMapper::toShortCommentDto)
                .collect(Collectors.toList());
    }
}
