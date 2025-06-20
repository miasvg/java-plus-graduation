package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventRequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService{
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;

    @Override
    public List<EventRequestDto> getUsersRequests(Long userId) {

        return List.of();
    }

    @Override
    public EventRequestDto createRequest(Long userId, Long eventId) {
        return null;
    }

    @Override
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        return null;
    }
}
