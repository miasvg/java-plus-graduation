package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.EventRequest;
import ru.practicum.event.model.Status;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    List<EventRequest> findAllByRequester_Id(Long requesterId);

    Optional<EventRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    @Modifying
    @Query("UPDATE EventRequest e SET e.status = :status WHERE e.id = :eventRequestId")
    EventRequest updateStatus(@Param("eventRequestId") Long eventRequestId, @Param("status")Status status);

    List<EventRequest> findAllByEventId(Long eventId);
}
