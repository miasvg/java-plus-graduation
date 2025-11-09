package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.UserAction;

import java.util.Arrays;
import java.util.List;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    UserAction findByEventIdAndUserId(Long eventId, Long userId);

    @Query("SELECT ua.eventId FROM UserAction ua WHERE ua.userId = :userId GROUP BY ua.eventId ORDER BY MAX(ua.timestamp) DESC")
    List<Long> findEventIdsOrderByMaxMarkDesc(Long userId, Pageable pageable);

    @Query("""
                SELECT ua.eventId, ua.mark
                FROM UserAction ua
                WHERE ua.userId = :userId
            """)
    List<Object[]> findEventMarksByUserId(Long userId);


    @Query("SELECT ua.eventId FROM UserAction ua WHERE ua.userId = :userId")
    List<Long> findEventIdsByUserId(Long userId);

    @Query("""
            SELECT ua.eventId, SUM(ua.mark)
            FROM UserAction ua
            WHERE ua.eventId IN :eventIds
            GROUP BY ua.eventId
            """)
    List<Object[]> sumMarksForEvents(List<Long> eventIds);

    List<UserAction> findAllByUserId(Long userId);
}
