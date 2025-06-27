package ru.practicum.event.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventRequestService;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;
    private final StatClient statClient;
    private final EventRequestService eventRequestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventRequest request) {
        log.info("Сохранение мероприятия");
        return eventService.addEvent(userId, request);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @Valid @RequestBody UpdateEventRequest request) {
        log.info(String.format("Обновление события с id %s пользователем с id %d", eventId, userId));
        return eventService.updateEventByUser(userId, eventId, request);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable("userId") long userId,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest request) {
        Pageable page = PageRequest.of(from, size);
        RequestHitDto hitDto = RequestHitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Отправляем данные по запросу getEventsByUser в сервис статистики {}", hitDto.toString());
        statClient.sendHit(hitDto);
        return eventService.getUsersEvents(userId, page, request.getRemoteAddr());
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@PathVariable("userId") long userId,
                                        @PathVariable("userId") long eventId,
                                        HttpServletRequest request) {
        log.info("Получение конкретной информации для конкретного пользователя о мероприятии");
        RequestHitDto hitDto = RequestHitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Отправляем данные по запросу getEventById в сервис статистики {}", hitDto.toString());
        statClient.sendHit(hitDto);
        return eventService.getByIdPrivate(userId, eventId, request.getRemoteAddr());
    }

    @GetMapping("/{userId}/requests")
    public List<EventRequestDto> getUsersEventList(@PathVariable Long userId,
                                                   HttpServletRequest request) {
        log.info("Получение запросов на учатие в событии пользователя с id {}", userId);
        return eventRequestService.getUsersRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public EventRequestDto createUserRequestToEvent(@PathVariable Long userId,
                                                    @RequestParam Long eventId) {
        log.info("Создание запроса на участие события с id: {} пользователем id: {}", eventId, userId);
        return eventRequestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public EventRequestDto cancelUserRequestToEvent(@PathVariable Long userId,
                                                    @PathVariable Long requestId) {
        log.info("Отмена запроса с id: {} пользователемс id: {}", requestId, userId);
        return eventRequestService.cancelRequest(userId, requestId);
    }
}
