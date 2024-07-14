package ru.practicum.api.privateApi.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.api.privateApi.comments.servise.PrivateCommentService;
import ru.practicum.dto.comments.CommentDto;
import ru.practicum.dto.comments.NewCommentDto;
import ru.practicum.dto.comments.UpdateCommentDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class PrivateCommentsController {

    private final PrivateCommentService privateCommentService;

    @PostMapping("/user/{userId}/event/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@RequestBody @Valid NewCommentDto newCommentDto,
                                  @PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long eventId) {
        return privateCommentService.saveComment(newCommentDto, userId, eventId);
    }

    @GetMapping("/{comId}")
    public CommentDto getCommentById(@PathVariable @Positive Long comId) {
        return privateCommentService.findCommentById(comId);
    }

    @GetMapping
    public Collection<CommentDto> getAllCommentsByUserIdOrEventId(@RequestParam(required = false) @Positive Long userId,
                                                                  @RequestParam(required = false) @Positive Long eventId) {
        return privateCommentService.findCommentsByUserId(userId, eventId);
    }

    @PatchMapping("/{comId}/user/{userId}")
    public CommentDto patchCommentById(@PathVariable @Positive Long userId,
                                       @PathVariable @Positive Long comId,
                                       @RequestBody @Valid UpdateCommentDto updateCommentDto) {
        return privateCommentService.updateCommentById(userId, comId, updateCommentDto);
    }

    @DeleteMapping("/{comId}/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable @Positive Long userId,
                                  @PathVariable @Positive Long comId) {
        privateCommentService.deleteCommentById(userId, comId);
    }

}
