package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.event.model.State;
import ru.practicum.event.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private Status status;
    private LocalDateTime created;
}
