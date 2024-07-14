package ru.practicum.dto.comments;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentParam {
    private Collection<String> state;
    private Long comId;
    private Long userId;
    private LocalDateTime start;
    private LocalDateTime end;
}
