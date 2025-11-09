package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EventSimilarity;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {

    boolean existsByEventAAndEventB(Long eventA, Long eventB);

    EventSimilarity findByEventAAndEventB(Long eventA, Long eventB);

    @Query("""
            SELECT es FROM EventSimilarity es
            WHERE
                (es.eventA IN :eventIds AND es.eventB NOT IN :eventIds)
             OR (es.eventA NOT IN :eventIds AND es.eventB IN :eventIds)
            ORDER BY es.score DESC
            """)
    List<EventSimilarity> findSimilarEventsExcludingUserEvents(List<Long> eventIds, Pageable pageable);

    @Query("""
                SELECT ua.eventId, ua.mark
                FROM UserAction ua
                WHERE ua.userId = :userId
            """)
    List<Object[]> findEventMarksByUserId(Long userId);


    @Query("""
            SELECT e FROM EventSimilarity e
            WHERE (e.eventA IN :eventIds AND e.eventB NOT IN :eventIds)
               OR (e.eventB IN :eventIds AND e.eventA NOT IN :eventIds)
            """)
    List<EventSimilarity> findSimilarPairsForEvents(List<Long> eventIds);

    List<EventSimilarity> findByEventAOrEventB(Long eventA, Long eventB);
}
