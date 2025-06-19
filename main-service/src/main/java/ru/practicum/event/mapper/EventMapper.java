package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventDtoPrivate;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.user.mappers.UserMapper;
import ru.practicum.user.model.User;

public class EventMapper {
    public static Event mapToEventNew(NewEventRequest request, Category category,
                                      Location location, User user) {
        Event event = Event.builder()
                .title(request.getTitle())
                .annotation(request.getAnnotation())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .category(category)
                .location(location)
                .paid(request.getPaid())
                .requestModeration(request.getRequestModeration())
                .initiator(user)
                .state(State.PENDING)
                .build();
        if (request.hasParticipantLimit()) {
            event.setParticipantLimit(request.getParticipantLimit());
        } else {
            event.setParticipantLimit(0);
        }
        return event;
    }

    public static EventDtoPrivate mapToDtoPrivate(Event event) {
        return EventDtoPrivate.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .location(LocationMapper.mapToDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .initiator(UserMapper.mapToUserDto(event.getInitiator()))
                .state(event.getState())
                .build();
    }
    public static EventShortDto mapToShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}
