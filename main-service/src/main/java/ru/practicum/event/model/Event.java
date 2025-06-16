package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.location.model.Location;
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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    String title;

    //краткая аннотация
    @Column(name = "annotation", nullable = false)
    String annotation;

    //полное описание
    @Column(name = "description", nullable = false)
    String description;

    //дата мероприятия
    @Column(name = "eventDate", nullable = false)
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    //платно или бесплатно, default = false
    @Column(name = "paid")
    Boolean paid;

    //лимит участников, default = 0, при значении 0 количество не ограничено
    @Column(name = "participant_limit")
    int participantLimit;

    //премодерация заявок, false = заявки принимаются автоматически, true = заявки требуют подтверждения
    @Column(name = "request_moderation")
    Boolean requestModeration;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    //счетчик просмотров
    @Column(name = "views")
    int views;

    @Enumerated(EnumType.STRING)
    State state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return participantLimit == event.participantLimit && views == event.views && Objects.equals(id, event.id)
                && Objects.equals(title, event.title) && Objects.equals(annotation, event.annotation)
                && Objects.equals(description, event.description) && Objects.equals(eventDate, event.eventDate)
                && Objects.equals(category, event.category) && Objects.equals(location, event.location)
                && Objects.equals(paid, event.paid) && Objects.equals(requestModeration, event.requestModeration)
                && Objects.equals(initiator, event.initiator) && state == event.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, annotation, description, eventDate, category, location, paid, participantLimit,
                requestModeration, initiator, views, state);
    }
}
