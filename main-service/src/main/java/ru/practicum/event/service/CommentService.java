package ru.practicum.event.service;

import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getCommentsByUser(Long userId);

    List<CommentDto> getCommentsByEvent(Long eventId);

    CommentDto createComment(Long userId, Long eventId, NewCommentDto dto);

    CommentDto updateComment(Long userId, Long commentId, NewCommentDto dto);

    void deleteComment(Long userId, Long commentId);

    void deleteCommentByAdmin(Long commentId);
}

