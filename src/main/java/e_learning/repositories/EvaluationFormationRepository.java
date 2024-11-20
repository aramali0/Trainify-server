package e_learning.repositories;

import e_learning.entity.EvaluationFormation;
import e_learning.enums.EvaluationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EvaluationFormationRepository extends JpaRepository<EvaluationFormation, Long> {
    List<EvaluationFormation> findByType(EvaluationType type);

    List<EvaluationFormation> findByEntrepriseIdAndType(Long enterpriseId, EvaluationType evaluationType);

    List<EvaluationFormation> findByEntrepriseId(Long enterpriseId);
}
