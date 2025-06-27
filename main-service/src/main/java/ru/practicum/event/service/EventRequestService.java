package ru.practicum.event.service;

import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.model.EventRequest;

import java.util.List;
import java.util.Optional;

public interface EventRequestService {
    List<EventRequestDto> getUsersRequests(Long userId);

    EventRequestDto createRequest(Long userId, Long eventId);

    EventRequestDto cancelRequest(Long userId, Long requestId);

    List<EventRequestDto> getAllByEventId(Long userId, Long eventId);

}
