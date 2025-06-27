package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.mapper.EventRequestMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventRequest;
import ru.practicum.event.model.Status;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventRequestRepository;
import ru.practicum.exeption.EventNotFoundException;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.exeption.NotValidUserException;
import ru.practicum.exeption.UserNotExistException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.event.mapper.EventRequestMapper.mapToEventRequestDto;

@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;

    @Override
    public List<EventRequestDto> getUsersRequests(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotExistException(userId);
        }

        return eventRequestRepository.findAllByRequester_Id(userId).stream()
                .map(EventRequestMapper::mapToEventRequestDto).toList();
    }

    @Override
    public EventRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotExistException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EventNotFoundException("Событие с id: " + eventId + "не существует."));
        EventRequest eventRequest = eventRequestRepository.save(EventRequest.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(Status.PENDING)
                .build());
        return mapToEventRequestDto(eventRequest);
    }

    @Override
    @Transactional
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest eventRequest = eventRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("EventRequest", requestId));
        if (!eventRequest.getRequester().getId().equals(userId)) {
            throw new NotValidUserException(userId);
        }
        eventRequest.setStatus(Status.CANCELED);
        EventRequest updatedRequest = eventRequestRepository.save(eventRequest);
        return mapToEventRequestDto(updatedRequest);
    }
}
