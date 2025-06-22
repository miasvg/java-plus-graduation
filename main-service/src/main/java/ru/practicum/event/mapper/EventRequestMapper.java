package ru.practicum.event.mapper;

import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.model.EventRequest;

public class EventRequestMapper {

    public static EventRequestDto mapToEventRequestDto(EventRequest eventRequest) {
        return EventRequestDto.builder()
                .id(eventRequest.getId())
                .requester(eventRequest.getRequester().getId())
                .event(eventRequest.getEvent().getId())
                .status(eventRequest.getStatus())
                .created(eventRequest.getCreated())
                .build();
    }
}
