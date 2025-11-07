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
import ru.practicum.dto.*;
import ru.practicum.event.service.EventService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody @Valid NewEventRequest request) {
        log.info("Сохранение мероприятия");
        return eventService.addEvent(userId, request);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long eventId,
                                    @Valid @RequestBody UpdateEventRequest request) {
        log.info(String.format("Обновление события с id %s пользователем с id %d", eventId, userId));
        return eventService.updateEventByUser(userId, eventId, request);
    }

    @GetMapping
    public List<EventShortDto> getEventsByUser(@PathVariable("userId") long userId,
                                               @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest request) {
        Pageable page = PageRequest.of(from, size);
        return eventService.getUsersEvents(userId, page, request.getRemoteAddr());
    }


    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable("userId") long userId,
                                     @PathVariable("eventId") long eventId,
                                     HttpServletRequest request) {
        log.info("Получение конкретной информации для конкретного пользователя о мероприятии");
        return eventService.getByIdPrivate(userId, eventId, request.getRemoteAddr());
    }

}

