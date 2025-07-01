package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.service.CommentService;
import ru.practicum.event.dto.NewCommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/comments")
public class UserCommentController {

    private final CommentService service;

    @GetMapping
    public List<CommentDto> getUserComments(@PathVariable Long userId) {
        return service.getCommentsByUser(userId);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getEventComments(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        return service.getCommentsByEvent(eventId);
    }

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody NewCommentDto dto) {
        return service.createComment(userId, eventId, dto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long userId,
                                  @PathVariable Long commentId,
                                  @Valid @RequestBody NewCommentDto dto) {
        return service.updateComment(userId, commentId, dto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        service.deleteComment(userId, commentId);
    }
}

