package ru.practicum.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.model.UserDto.UserDto;

import java.time.LocalDateTime;
import java.util.Objects;

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
    int views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDtoPrivate that = (EventDtoPrivate) o;
        return participantLimit == that.participantLimit && views == that.views
                && Objects.equals(id, that.id) && Objects.equals(title, that.title)
                && Objects.equals(annotation, that.annotation) && Objects.equals(description, that.description)
                && Objects.equals(eventDate, that.eventDate) && Objects.equals(category, that.category)
                && Objects.equals(location, that.location) && Objects.equals(paid, that.paid)
                && Objects.equals(requestModeration, that.requestModeration) && Objects.equals(initiator, that.initiator)
                && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, annotation, description, eventDate, category, location, paid, participantLimit,
                requestModeration, initiator, state, views);
    }
}
