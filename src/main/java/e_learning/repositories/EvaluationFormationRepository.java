package e_learning.repositories;

import e_learning.entity.EvaluationFormation;
import e_learning.enums.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface EvaluationFormationRepository extends JpaRepository<EvaluationFormation, Long> {

//    List<EvaluationFormation> findByType(EvaluationType type);
//

    List<EvaluationFormation> findByCourEntrepriseResponsableFormationsUserId(Long userId);

    List<EvaluationFormation> findByCourEntrepriseParticipantsUserIdAndType(Long userId, EvaluationType type);
    List<EvaluationFormation> findByCourEntrepriseFormateursUserIdAndType(Long userId , EvaluationType type);
}
