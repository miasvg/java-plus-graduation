package ru.practicum.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.dto.NewCommentDto;
import ru.practicum.event.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toEntity(NewCommentDto dto, User user, Event event) {
        return Comment.builder()
                .text(dto.getText())
                .created(LocalDateTime.now())
                .creator(user)
                .event(event)
                .build();
    }

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .creatorId(comment.getCreator().getId())
                .eventId(comment.getEvent().getId())
                .build();
    }
}
