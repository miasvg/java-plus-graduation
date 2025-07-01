package ru.practicum.event.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private LocalDateTime created;
    private Long creatorId;
    private Long eventId;
}
