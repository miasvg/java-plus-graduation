package ru.practicum.event.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestUpdateDto {
    List<Long> requestIds;
    String status;
}
