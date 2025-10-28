package ru.practicum.eventRequest.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.enums.State;
import ru.practicum.eventRequest.mapper.EventRequestMapper;
import ru.practicum.eventRequest.model.EventRequest;

import ru.practicum.enums.Status;

import ru.practicum.eventRequest.repository.EventRequestRepository;
import ru.practicum.exeption.*;
import ru.practicum.feign.FeignEventClient;
import ru.practicum.feign.FeignUserClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static ru.practicum.eventRequest.mapper.EventRequestMapper.mapToEventRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventRequestServiceImpl implements EventRequestService {
    private final FeignUserClient userClient;
    private final FeignEventClient eventClient;
    private final EventRequestRepository eventRequestRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventRequestDto> getUsersRequests(Long userId) {
        userClient.getUserById(userId).orElseThrow(() -> new NotFoundException("EventRequest", userId));
        return eventRequestRepository.findAllByRequesterId(userId).stream()
                .map(EventRequestMapper::mapToEventRequestDto).toList();
    }

    @Transactional
    @Override
    public EventRequestDto createRequest(Long userId, Long eventId) {
        log.info("Начинаем создание заявки на участие в мероприятии id = {} от пользователя id = {}", eventId, userId);
        UserDto user = userClient.getUserById(userId).orElseThrow(() -> new NotFoundException("EventRequest", userId));
        EventFullDto event = eventClient.getEventById(eventId).orElseThrow(() ->
                new NotFoundException("EventRequest", eventId));
        Optional<EventRequest> request = eventRequestRepository.findByEventIdAndRequesterId(eventId, userId);
        if (request.isPresent()) {
            throw new RequestModerationException(eventId, "Заявка уже была отправлена");
        }

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            log.error("Заявка не была добавлена: лимит заявок исчерпан: лимит={}, принятых заявок={}",
                    event.getParticipantLimit(), event.getConfirmedRequests());
            throw new RequestModerationException(eventId, "Лимит заявок исчерпан");
        }
        if (event.getInitiator().getId().equals(userId)) {
            log.error("Заявка не была отправлена: нельзя отправить заявку на собственное мероприятие");
            throw new RequestModerationException(eventId, "Нельзя отправить заявку на собственное мероприятие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            log.error("Заявка не была отправлена: нельзя отправить заявку на неопубликованное мероприятие: {}",
                    event.getState());
            throw new RequestModerationException(eventId, "Нельзя отправить заявку на неопубликованное мероприятие");
        }

        EventRequest eventRequest = EventRequest.builder()
                .created(LocalDateTime.now())
                .requesterId(user.getId())
                .eventId(event.getId())
                .status(Status.PENDING)
                .build();
        if (event.getParticipantLimit() == 0) {
            log.info("Статус заявки автоматически изменен на CONFIRMED, лимит заявок для Event id={} не установлен",
                    eventId);
            eventRequest.setStatus(Status.CONFIRMED);
            log.info("___Подтверждение заявки на событие с id {}", eventRequest.getEventId());
            updateConfirmedRequest(event, 1);
        }

        if (!event.getRequestModeration()) {
            log.info("Статус заявки автоматически изменен на CONFIRMED, модерация заявок для Event id={} не установлена",
                    eventId);
            eventRequest.setStatus(Status.CONFIRMED);
            log.info("___Подтверждение заявки на событие с id {}", eventRequest.getEventId());
            updateConfirmedRequest(event, 1);
        }
        EventRequest savedRequest = eventRequestRepository.save(eventRequest);
        log.info("--------Заявка успешно сохранена: {}", savedRequest);
        return mapToEventRequestDto(savedRequest);
    }

    @Override
    @Transactional
    public EventRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отменяем заявку id={} пользователем id={}", requestId, userId);
        EventRequest eventRequest = eventRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("EventRequest", requestId));
        if (!eventRequest.getRequesterId().equals(userId)) {
            throw new NotValidUserException(userId);
        }
        int i = eventRequestRepository.updateStatus(requestId, Status.CANCELED);
        if (i == 1) {
            log.info("Заявка отменена");
            eventRequest.setStatus(Status.CANCELED);
        }
        return mapToEventRequestDto(eventRequest);
    }

    @Transactional
    @Override
    public List<EventRequestDto> getAllByEventId(Long userId, Long eventId) {
        log.info("Поиск заявок на участие от пользователя id={} для Event id={}", userId, eventId);
        userClient.getUserById(userId).orElseThrow(() -> new NotFoundException("EventRequest", userId));
        eventClient.getEventById(eventId).orElseThrow(() ->
                new NotFoundException("EventRequest", eventId));
        return eventRequestRepository.findAllByEventId(eventId).stream()
                .map(EventRequestMapper::mapToEventRequestDto)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public EventRequestUpdateResult updateRequestState(Long userId, Long eventId,
                                                       EventRequestUpdateDto updateDto) {
        log.info("Начинаем обновление заявок для событий id={} пользователем id={}", eventId, userId);

        EventRequestUpdateResult result = EventRequestUpdateResult.builder().build();

        String status = updateDto.getStatus();
        List<Long> requestIds = updateDto.getRequestIds();

        UserDto user = userClient.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        log.info("Определен инициатор события {}", user);
        EventFullDto event = eventClient.getEventById(eventId)
                .orElseThrow(() -> new NotFoundException("Event", eventId));
        log.info(" EventDTO INFO : {}", event);

        List<EventRequest> requests = eventRequestRepository.findByRequestIds(requestIds);
        if (requests.stream().anyMatch(eventRequest -> !eventRequest.getStatus().equals(Status.PENDING))) {
            log.error("В списке есть заявка не находящаяся в статусе ожидания");
            throw new RequestModerationException(eventId, "Можно принимать заявки только в статусе ожидания");
        }

        if (status.equalsIgnoreCase("rejected")) {
            updateStatusAllRequest(requestIds, Status.REJECTED);
            result.setRejectedRequests(findAllByListIds(requestIds));
            return result;
        }

        int limit = event.getParticipantLimit();
        int confirmed = event.getConfirmedRequests();

        log.info("Проверяем наличие свободных мест");
        if (limit > 0 && limit <= confirmed) {
            log.error("Лимит заявок для мероприятия id={} уже исчерпан {}", eventId, limit - confirmed);
            throw new RequestModerationException(eventId, "Лимит заявок исчерпан");
        }

        int checkLimit = limit - confirmed;
        log.info("Вычисляем количество свободных мест {}", checkLimit);

        List<Long> toConfirm;
        List<Long> toReject = List.of();

        if (requests.size() > checkLimit) {
            toConfirm = requestIds.subList(0, checkLimit);
            toReject = requestIds.subList(checkLimit, requestIds.size());
        } else {
            toConfirm = requestIds;
        }

        if (!toConfirm.isEmpty()) {
            updateStatusAllRequest(toConfirm, Status.CONFIRMED);
            result.setConfirmedRequests(findAllByListIds(toConfirm));
            updateConfirmedRequest(event, toConfirm.size());
        }
        if (!toReject.isEmpty()) {
            updateStatusAllRequest(toReject, Status.REJECTED);
            result.setRejectedRequests(findAllByListIds(toReject));
        }
        return result;
    }

    @Override
    public Optional<EventRequestDto> getByEventIdAndRequesterId(Long eventId, Long userId) {
        return eventRequestRepository.findByEventIdAndRequesterId(eventId, userId)
                .map(EventRequestMapper::mapToEventRequestDto);
    }

    @Transactional
    public List<EventRequestDto> findAllByListIds(List<Long> ids) {
        log.info("Получаем все обновленные заявки по id={}", ids);
        entityManager.clear();
        List<EventRequest> requests = eventRequestRepository.findByIdIn(ids);
        log.info("Возвращаем список обновленных заявок {}", requests);
        return requests.stream()
                .map(EventRequestMapper::mapToEventRequestDto)
                .toList();
    }

    @Transactional
    public void updateStatusAllRequest(List<Long> ids, Status status) {
        log.info("Обновляем статус для заявок id={} на {}", ids, status);
        int update = eventRequestRepository.updateStatusForRequestsIds(ids, status);
        entityManager.flush();
        log.info("Количество обновленных записей: {}", update);
    }

    private void updateConfirmedRequest(EventFullDto event, int increment) {
        log.info("Мы обновляем для even : {}, увеличивая подтверждение на Increment {}", event, increment);
        if (!eventClient.incrementConfirmedRequests(event.getId(), increment)) {
            throw new TimeOutException("Не получилось обновить");
        }
    }
}
