package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventRequestDto;
import java.util.Optional;

@FeignClient(name = "REQUEST-SERVICE", fallback = FeignRequestClientFallback.class)
public interface FeignRequestClient {

    @GetMapping("/users/{userId}/requests/{requestId}/feign")
    Optional<EventRequestDto> getByEventIdAndRequesterId(@PathVariable Long eventId, @PathVariable Long userId);
}
