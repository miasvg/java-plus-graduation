package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiatorIdAndState(Long initiator_id, String state, Pageable page);

    Optional<Event> findByIdAndState(Long id, String state);

    Page<Event> findAll(Specification<Event> spec, Pageable pageable);

    @Modifying
    @Query("UPDATE Event e SET e.views = e.views + 1 WHERE e.id IN :ids")
    void incrementViews(@Param("ids") List<Long> ids);
}
