package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    CategoryDto category;
    LocationDto location;
    Boolean paid;
    int participantLimit;
    Boolean requestModeration;
    UserDto initiator;
    State state;
    int views;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;

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

    @Override
    public String toString() {
        return "EventDtoPrivate{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", annotation='" + annotation + '\'' +
                ", description='" + description + '\'' +
                ", eventDate=" + eventDate +
                ", category=" + category +
                ", location=" + location +
                ", paid=" + paid +
                ", participantLimit=" + participantLimit +
                ", requestModeration=" + requestModeration +
                ", initiator=" + initiator +
                ", state=" + state +
                ", views=" + views +
                ", createdOn=" + createdOn +
                '}';
    }
}
