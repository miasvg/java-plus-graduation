package ru.practicum.event.service;

import ru.practicum.event.dto.EventRequestDto;

import java.util.List;

public interface EventRequestService {
    List<EventRequestDto> getUsersRequests(Long userId);

    EventRequestDto createRequest(Long userId, Long eventId);

    EventRequestDto cancelRequest(Long userId, Long requestId);
}
