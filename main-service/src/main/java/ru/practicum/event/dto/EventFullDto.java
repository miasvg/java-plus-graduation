package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.model.UserDto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String title;
    String annotation;
    CategoryDto category;
    Boolean paid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    UserShortDto initiator;
    Integer views;
    Integer confirmedRequests;
    String description;
    Integer participantLimit;
    State state;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    LocationDto location;
    Boolean requestModeration;

}
