package ru.practicum.event.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.dto.*;

import ru.practicum.enums.State;
import ru.practicum.event.model.Event;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;


import java.time.LocalDateTime;

public class EventMapper {
    public static Event mapToEventNew(NewEventRequest request, Category category,
                                      Location location, UserDto user) {
        Event event = Event.builder()
                .title(request.getTitle())
                .annotation(request.getAnnotation())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .category(category)
                .location(location)
                .paid(request.getPaid())
                .requestModeration(request.getRequestModeration())
                .initiator(user.getId())
                .initiatorName(user.getName())
                .state(State.PENDING)
                .createdOn(LocalDateTime.now())
                .build();
        if (request.hasParticipantLimit()) {
            event.setParticipantLimit(request.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }
        return event;
    }

    public static EventShortDto mapToShortDto(Event event) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(event.getInitiator())
                .name(event.getInitiatorName())
                .build();
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .description(event.getDescription())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .location(LocationMapper.mapToDto(event.getLocation()))
                .requestModeration(event.getRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static EventFullDto mapToFullDto(Event event, double rating) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(event.getInitiator())
                .name(event.getInitiatorName())
                .build();

        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(userShortDto)
                .location(LocationMapper.mapToDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .rating(rating)
                .build();
    }
}