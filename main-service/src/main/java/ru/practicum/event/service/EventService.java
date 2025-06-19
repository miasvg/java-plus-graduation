package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.NewEventRequest;

import java.util.List;

public interface EventService {
    EventDtoPrivate addEvent(Long userId, NewEventRequest request);

    EventDtoPrivate getById(Long eventId);

    List<EventDtoPrivate> getUsersEvents(Long userId, Pageable page);
}
