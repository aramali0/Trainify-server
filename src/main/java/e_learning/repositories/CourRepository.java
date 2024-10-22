package e_learning.repositories;

import e_learning.entity.ClassEntity;
import e_learning.entity.Cour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CourRepository extends JpaRepository<Cour, Long> {
    Collection<Cour> findByEntrepriseResponsableFormationsUserId(Long responsableFormationId);

    Collection<Cour> findByEntrepriseResponsableFormationsEmail(String email);

    Collection<Cour> findByClassesId(Long classes_id);

    Collection<Cour> findByFormateursUserId(Long formateurId);

    Collection<Cour> findByClassesParticipantsUserId(Long participantId);

    @Modifying
    @Query("UPDATE Cour c SET c.isApproved = false WHERE c.id = :id")
    void blockCour(Long id);

    @Modifying
    @Query("UPDATE Cour c SET c.isApproved = true WHERE c.id = :id")
    void unblockCour(Long id);

    Collection<Cour> findByEntrepriseChargeFormationsUserId(Long id);
}
