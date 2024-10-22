package e_learning.repositories;

import e_learning.entity.Section;
import e_learning.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session,Long> {
    List<Session> findByCreatedByUserId(Long userId);

    List<Session> findSessionsByCourId(Long aLong);
}
