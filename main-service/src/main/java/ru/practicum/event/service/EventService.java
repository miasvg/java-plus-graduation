package ru.practicum.event.service;

import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventRequest;
import ru.practicum.event.dto.UpdateEventUserRequest;

public interface EventService {
    EventDtoPrivate addEvent(Long userId, NewEventRequest request);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request);
}
