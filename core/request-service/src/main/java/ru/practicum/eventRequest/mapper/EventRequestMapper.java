package ru.practicum.eventRequest.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EventRequestDto;
import ru.practicum.eventRequest.model.EventRequest;

@Component
public class EventRequestMapper {

    public static EventRequestDto mapToEventRequestDto(EventRequest eventRequest) {
        return EventRequestDto.builder()
                .id(eventRequest.getId())
                .requester(eventRequest.getRequesterId())
                .event(eventRequest.getEventId())
                .status(eventRequest.getStatus())
                .created(eventRequest.getCreated())
                .build();
    }
}
