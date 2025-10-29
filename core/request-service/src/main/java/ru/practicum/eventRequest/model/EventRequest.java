package ru.practicum.eventRequest.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.enums.Status;


import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "requester_id")
    private Long requesterId;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created")
    private LocalDateTime created;
}
