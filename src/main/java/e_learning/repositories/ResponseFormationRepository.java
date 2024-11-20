package e_learning.repositories;

import e_learning.entity.ResponseFormation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseFormationRepository extends JpaRepository<ResponseFormation, Long> {
    List<ResponseFormation> findByEvaluationId(Long evaluationId);
}
