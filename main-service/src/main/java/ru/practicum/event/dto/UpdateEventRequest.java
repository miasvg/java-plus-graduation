package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.location.dto.NewLocationRequest;
import ru.practicum.location.model.Location;
import ru.practicum.validator.ValidEventDate;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    String annotation;
    Integer category;
    String description;

    @ValidEventDate(allowNull = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;

    NewLocationRequest location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;
    String stateAction;
    String title;
}
