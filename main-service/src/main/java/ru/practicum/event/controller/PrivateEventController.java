package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.NewEventRequest;
import ru.practicum.event.service.EventRequestService;
import ru.practicum.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;
    private final EventRequestService eventRequestService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDtoPrivate addEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventRequest request) {
        log.info("Сохранение мероприятия");
        return eventService.addEvent(userId, request);
    }

    @GetMapping("/{userId}/requests")
    public List<EventRequestDto> getUsersEventList(@PathVariable Long userId) {
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

    @PatchMapping("/{userId}/request/{requestId}/cancel")
    public EventRequestDto cancelUserRequestToEvent(@PathVariable Long userId,
                                                    @PathVariable Long requestId) {
        log.info("Отмена запроса с id: {} пользователемс id: {}", requestId, userId);
        return eventRequestService.cancelRequest(userId, requestId);
    }
}
