package ru.practicum.feign;

import ru.practicum.dto.EventRequestDto;

import java.util.Optional;

public class FeignRequestLikeClientFallback implements FeingLikeRequestClient{
    @Override
    public Optional<EventRequestDto> getByEventIdAndRequesterId(Long eventId, Long userId) {
        return Optional.empty();
    }
}
