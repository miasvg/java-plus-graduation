package ru.practicum.event.service;

import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.NewEventRequest;

public interface EventService {
    EventDtoPrivate addEvent(Long userId, NewEventRequest request);
}
