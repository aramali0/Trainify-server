package e_learning.repositories;

import e_learning.entity.Cour;
import e_learning.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant,Long> {
    Optional<Participant> findByUserId(Long participantId);

    Collection<Participant> findParticipantsByClassesId(Long id);

    List<Participant> findByClassesCoursSessionsSectionsId(Long sectionId);
}
