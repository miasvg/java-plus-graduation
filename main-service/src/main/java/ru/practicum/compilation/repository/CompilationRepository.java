package ru.practicum.compilation.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query("""
            SELECT c FROM Compilation
            WHERE (:pinned IS NULL OR c.pinned = :pinned)
            """)
    List<Compilation> findAllWithFilter(Boolean pinned, Pageable pageable);
}
