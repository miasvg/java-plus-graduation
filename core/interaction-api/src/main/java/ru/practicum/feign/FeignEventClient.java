package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import ru.practicum.dto.EventFullDto;

import java.util.Optional;

@FeignClient(name = "EVENT-SERVICE", fallback = FeignEventClientFallback.class)
public interface FeignEventClient {

    @GetMapping("/events/{eventId}/feign")
    Optional<EventFullDto> getEventById(@PathVariable("eventId") Long eventId);

    @GetMapping("/events/{eventId}/{userId}/feign")
    Optional<EventFullDto> getByIdAndInitiator(@PathVariable("eventId") Long eventId,
                                               @PathVariable("userId") Long userId);

    @PutMapping("/events/{eventId}/{increment}/feign")
    Boolean incrementConfirmedRequests(@PathVariable("eventId") Long eventId,
                                       @PathVariable("increment") Integer increment);
}
