package ru.practicum.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventSearchParam;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventRequest;
import ru.practicum.event.dto.UpdateEventRequest;

import java.util.List;
import java.util.Optional;

public interface EventService {
    EventDtoPrivate addEvent(Long userId, NewEventRequest request);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventRequest request);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventRequest request);

    Optional<EventDtoPrivate> getByIdPublic(Long eventId);

    List<EventShortDto> getUsersEvents(Long userId, Pageable page);

    EventDtoPrivate getByIdPrivate(Long userId, Long eventId);

    List<EventShortDto> getEventsWithParamAdmin(EventSearchParam eventSearchParam, Pageable page);

    List<EventShortDto> getEventsWithParamPublic(EventSearchParam eventSearchParam, Pageable page);
}
