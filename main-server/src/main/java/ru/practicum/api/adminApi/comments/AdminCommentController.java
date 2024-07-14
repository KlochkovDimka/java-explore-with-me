package ru.practicum.api.adminApi.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.adminApi.comments.service.AdminCommentService;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.CommentParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final AdminCommentService commentService;

    @GetMapping
    public Collection<CommentDto> getAllCommentByParam(@RequestParam(required = false) Collection<String> states,
                                                       @RequestParam(required = false) Long comId,
                                                       @RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        CommentParam commentParam = CommentParam.builder()
                .state(states)
                .comId(comId)
                .userId(userId)
                .start(start)
                .end(end)
                .build();

        PageRequest request = PageRequest.of(from / size, size);
        return commentService.findAllCommentByParam(commentParam, request);
    }

    @PatchMapping("{comId}")
    public CommentDto commentUpdateState(@Positive @PathVariable Long comId,
                                         @NotBlank @RequestParam String state) {
        return commentService.commentUpdateStatus(comId, state);
    }
}
