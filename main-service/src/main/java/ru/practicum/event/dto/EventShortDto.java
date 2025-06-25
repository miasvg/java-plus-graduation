package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.model.UserDto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {
    Long id;
    String title;
    String annotation;
    CategoryDto category;
    Boolean paid;
    LocalDateTime eventDate;
    UserShortDto initiator;
    int views;
    int confirmedRequests;
    String description;
    int participantLimit;
    State state;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    LocationDto location;
    Boolean requestModeration;

}
