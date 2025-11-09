package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.EventRequestDto;
import java.util.Optional;

@FeignClient(name = "REQUEST-LIKE-SERVICE", fallback = FeignRequestLikeClientFallback.class)
public interface FeingLikeRequestClient {
    @GetMapping("/users/{userId}/requests/{eventId}/like/feign")
    Optional<EventRequestDto> getByEventIdAndRequesterId(@PathVariable Long eventId, @PathVariable Long userId);
}
