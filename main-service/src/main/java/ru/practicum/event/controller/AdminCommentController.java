package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/comments")
public class AdminCommentController {

    private final CommentService service;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        service.deleteCommentByAdmin(commentId);
    }
}
