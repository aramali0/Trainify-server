package e_learning.repositories;

import e_learning.entity.Unavailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnavailabilityRepository extends JpaRepository<Unavailability, Long> {
    List<Unavailability> findByParticipantUserId(Long participantId);
}
