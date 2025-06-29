package ru.practicum.event.service;

import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventRequestUpdateDto;
import ru.practicum.event.dto.EventRequestUpdateResult;

import java.util.List;

public interface EventRequestService {
    List<EventRequestDto> getUsersRequests(Long userId);

    EventRequestDto createRequest(Long userId, Long eventId);

    EventRequestDto cancelRequest(Long userId, Long requestId);

    List<EventRequestDto> getAllByEventId(Long userId, Long eventId);

    EventRequestUpdateResult updateRequestState(Long userId, Long eventId, EventRequestUpdateDto updateDto);
}
