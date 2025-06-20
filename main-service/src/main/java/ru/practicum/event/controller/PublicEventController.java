package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatClient;
import ru.practicum.dto.RequestHitDto;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventSearchParam;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exeption.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private final StatClient statClient;

    @GetMapping("/{eventId}")
    public EventDtoPrivate getById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Получаем мероприятие по id = {}", eventId);
        Optional<EventDtoPrivate> event = eventService.getByIdPublic(eventId);
        if (event.isEmpty()) {
            log.error("Запрашиваемое мероприятие не найдено или еще не было опубликовано");
            throw new NotFoundException("Event", eventId);
        }
        RequestHitDto hitDto = RequestHitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Отправляем данные по запросу getById в сервис статистики {}", hitDto.toString());
        statClient.sendHit(hitDto);
        return event.orElseThrow();
    }

    @GetMapping
    public List<EventShortDto> getEventsWithParam(@RequestParam(value = "users", required = false) List<Long> users,
                                                  @RequestParam(value = "categories", required = false) List<Long> categories,
                                                  @RequestParam(value = "rangeStart", required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(value = "rangeEnd", required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size,
                                                  HttpServletRequest request) {
        log.info("Получаем мероприятия с фильтрацией");
        Pageable page = PageRequest.of(from, size);
        EventSearchParam eventSearchParam = EventSearchParam.builder()
                .users(users)
                .states(List.of("PUBLISHED"))
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();
        RequestHitDto hitDto = RequestHitDto.builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        log.info("Отправляем данные по запросу getEventsWithParam в сервис статистики {}", hitDto.toString());
        statClient.sendHit(hitDto);
        return eventService.getEventsWithParamPublic(eventSearchParam, page);
    }
}
