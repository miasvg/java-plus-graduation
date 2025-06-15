package repository;

import model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);
    boolean existByEmail(Long userId);

    @Query("SELECT u FROM User u WHERE u.id > :from")
    Page<User> findByIdAfter(Long from, Pageable pageable);

    List<User> findByIdIn(List<Long> ids);
}
