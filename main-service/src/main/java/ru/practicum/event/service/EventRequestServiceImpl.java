package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.mapper.EventRequestMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.model.Status;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.EventRequestRepository;
import ru.practicum.exeption.NotFoundException;
import ru.practicum.exeption.NotValidUserException;
import ru.practicum.exeption.RequestModerationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.event.mapper.EventRequestMapper.mapToEventRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;

    @Override
    public List<EventRequestDto> getUsersRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("EventRequest", userId));
        return eventRequestRepository.findAllByRequester_Id(userId).stream()
                .map(EventRequestMapper::mapToEventRequestDto).toList();
    }

    @Transactional
    @Override
    public EventRequestDto createRequest(Long userId, Long eventId) {
        log.info("Начинаем создание заявки на участие в мероприятии id = {} от пользователя id = {}", eventId, userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("EventRequest", userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("EventRequest", eventId));
        EventRequest request = eventRequestRepository.findByEventIdAndRequesterId(eventId, userId)
                .orElseThrow(() -> new RequestModerationException(eventId, "Заявка уже была отправлена"));
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
            log.error("Заявка не была добавлена: лимит заявок исчерпан: лимит={}, принятых заявок={}",
                    event.getParticipantLimit(), event.getConfirmedRequests());
            throw new RequestModerationException(eventId, "Лимит заявок исчерпан");
        }
        if (eventRepository.findByIdAndInitiatorId(eventId, userId).isPresent()) {
            log.error("Заявка не была отправлена: нельзя отправить заявку на собственное мероприятие");
            throw new RequestModerationException(eventId, "Нельзя отправить заявку на собственное мероприятие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("Заявка не была отправлена: нельзя отправить заявка на неопубликованное мероприятие: {}",
                    event.getState());
            throw new RequestModerationException(eventId, "Нельзя отправить заявку на неопубликованное мероприятие");
        }
        EventRequest eventRequest = EventRequest.builder()
                .created(LocalDateTime.now())
                .requester(user)
                .event(event)
                .status(Status.PENDING)
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            log.info("Статус заявки автоматически изменен на CONFIRMED, модерация заявок Event: {}", event.getRequestModeration());
            eventRequest.setStatus(Status.CONFIRMED);
        }
        log.info("Заявка успешно сохранена: {}", eventRequest);
        return mapToEventRequestDto(eventRequestRepository.save(eventRequest));
    }

    @Override
    @Transactional
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest eventRequest = eventRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("EventRequest", requestId));
        if (!eventRequest.getRequester().getId().equals(userId)) {
            throw new NotValidUserException(userId);
        }
        EventRequest updateRequest = eventRequestRepository.updateStatus(requestId, Status.CANCELED);
        return mapToEventRequestDto(updateRequest);
    }

    @Override
    public List<EventRequestDto> getAllByEventId(Long userId, Long eventId) {
        log.info("Поиск заявок на участие от пользователя id={} для Event id={}", userId, eventId);
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("EventRequest", userId));
        eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("EventRequest", eventId));
        return eventRequestRepository.findAllByEventId(eventId).stream()
                .map(EventRequestMapper::mapToEventRequestDto)
                .toList();
    }
}
