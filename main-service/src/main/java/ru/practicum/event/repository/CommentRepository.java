package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /* все комментарии автора */
    List<Comment> findByCreatorId(Long creatorId);

    /* все комментарии под событием */
    List<Comment> findByEventId(Long eventId);
}

