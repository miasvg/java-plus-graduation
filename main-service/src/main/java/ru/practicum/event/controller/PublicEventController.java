package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final StatClient statClient;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDtoPrivate getById(@PathVariable Long eventId, HttpServletRequest request) {
        RequestHitDto hitDto = RequestHitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Отправляем данные в сервис статистики {}", hitDto.toString());
        statClient.sendHit(hitDto);
        log.info("Получаем мероприятие по id = {}", eventId);
        return eventService.getById(eventId);
    }

}
