package ru.practicum.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.UserDto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {
    String annotation;
    CategoryDto category;
    int confirmedRequests;
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    Boolean paid;
    String title;
    int views;
}
