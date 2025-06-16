package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "created")
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id ")
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    @Enumerated(EnumType.STRING)
    RequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipationRequest that = (ParticipationRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(created, that.created) && Objects.equals(event, that.event)
                && Objects.equals(requester, that.requester) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, event, requester, status);
    }
}
