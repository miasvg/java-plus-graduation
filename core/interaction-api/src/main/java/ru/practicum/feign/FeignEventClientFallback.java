package ru.practicum.feign;

import ru.practicum.dto.EventFullDto;

import java.util.Optional;

public class FeignEventClientFallback implements FeignEventClient{
    @Override
    public Optional<EventFullDto> getEventById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<EventFullDto> getByIdAndInitiator(Long eventId, Long userId) {
        return Optional.empty();
    }

    @Override
    public Boolean incrementConfirmedRequests(Long eventId, Integer increment) {
        return Boolean.FALSE;
    }


}
