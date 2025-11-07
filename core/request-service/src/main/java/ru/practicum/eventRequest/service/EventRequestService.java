package ru.practicum.eventRequest.service;

import ru.practicum.dto.EventRequestDto;
import ru.practicum.dto.EventRequestUpdateDto;
import ru.practicum.dto.EventRequestUpdateResult;

import java.util.List;
import java.util.Optional;

public interface EventRequestService {
    List<EventRequestDto> getUsersRequests(Long userId);

    EventRequestDto createRequest(Long userId, Long eventId);

    EventRequestDto cancelRequest(Long userId, Long requestId);

    List<EventRequestDto> getAllByEventId(Long userId, Long eventId);

    EventRequestUpdateResult updateRequestState(Long userId, Long eventId, EventRequestUpdateDto updateDto);

    Optional<EventRequestDto> getByEventIdAndRequesterId(Long eventId, Long userId);
}