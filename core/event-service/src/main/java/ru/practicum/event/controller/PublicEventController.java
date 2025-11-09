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
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventSearchParam;
import ru.practicum.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import stats.messages.collector.UserAction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    private static final String EWM_ID = "X-EWM-USER-ID";

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable Long eventId, HttpServletRequest request,
                                @RequestHeader(EWM_ID) Long userId) {
        log.info("Получаем мероприятие для Public API по id = {}", eventId);

        log.info("Отправляем данные по запросу getById в сервис статистики userId {},eventId {}, action {}",
                userId, eventId, UserAction.ActionTypeProto.ACTION_VIEW);

        return eventService.getByIdPublic(eventId, request.getRemoteAddr(), userId);
    }

    @GetMapping
    public List<EventShortDto> getEventsWithParam(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false")
                                                  Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(defaultValue = "0", required = false)
                                                  @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10", required = false) @Positive Integer size,
                                                  HttpServletRequest request) {
        log.info("Получаем мероприятия с фильтрацией");
        Pageable page = PageRequest.of(from, size);
        EventSearchParam eventSearchParam = EventSearchParam.builder()
                .text(text)
                .states(List.of("PUBLISHED"))
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .build();
        return eventService.getEventsWithParamPublic(eventSearchParam, page, request.getRemoteAddr());
    }

    @GetMapping("/{eventId}/feign")
    public Optional<EventFullDto> getEventById(@PathVariable Long eventId) {
        return eventService.getEventByIdFeign(eventId);
    }

    @GetMapping("/{eventId}/{userId}/feign")
    public Optional<EventFullDto> getEventByIdAndInitiator(@PathVariable Long eventId,
                                                           @PathVariable Long userId) {
        return eventService.getEventByIdAndInitiator(eventId, userId);
    }

    @PutMapping("/{eventId}/{increment}/feign")
    public Boolean updateConfirmedRequests(@PathVariable Long eventId, @PathVariable Integer increment) {
        return eventService.updateConfirmedRequests(eventId, increment);
    }

    @GetMapping("/recommendations")
    public List<EventFullDto> getRecommendationEvent(@RequestHeader(EWM_ID) Long userId) {
        return eventService.getRecommendations(userId);
    }

    @PutMapping("/{eventId}/like")
    public void setLike(@RequestHeader(EWM_ID) Long userId, @PathVariable Long eventId) {
        eventService.setLike(userId, eventId);
    }
}
