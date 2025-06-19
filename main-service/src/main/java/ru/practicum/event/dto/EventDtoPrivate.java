package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.model.UserDto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventDtoPrivate {
    Long id;
    String title;
    String annotation;
    String description;
    LocalDateTime eventDate;
    CategoryDto category;
    LocationDto location;
    Boolean paid;
    int participantLimit;
    Boolean requestModeration;
    UserDto initiator;
    State state;

}
