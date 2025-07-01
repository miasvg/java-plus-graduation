package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.dto.NewCommentDto;
import ru.practicum.event.mapper.CommentMapper;
import ru.practicum.event.model.Comment;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exeption.ForbiddenException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final UserRepository userRepo;
    private final EventRepository eventRepo;
    private final CommentMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByUser(Long userId) {
        return commentRepo.findByCreatorId(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getCommentsByEvent(Long eventId) {
        return commentRepo.findByEventId(eventId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        Comment saved = commentRepo.save(mapper.toEntity(dto, user, event));
        return mapper.toDto(saved);
    }


    @Override
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto dto) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " not found"));

        if (!comment.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User not allowed to edit this comment");
        }

        comment.setText(dto.getText());
        return mapper.toDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " not found"));

        if (!comment.getCreator().getId().equals(userId)) {
            throw new ForbiddenException("User not allowed to delete this comment");
        }
        commentRepo.delete(comment);
    }

    @Override
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " not found");
        }
        commentRepo.deleteById(commentId);
    }
}

