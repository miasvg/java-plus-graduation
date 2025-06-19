package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.location.dto.NewLocationRequest;
import ru.practicum.validator.ValidEventDate;

import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {
    String annotation;
    Integer category;
    String description;
    @ValidEventDate
    LocalDateTime eventDate;
    NewLocationRequest location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    String title;

}
