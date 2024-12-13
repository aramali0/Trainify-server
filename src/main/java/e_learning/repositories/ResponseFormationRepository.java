package e_learning.repositories;

import e_learning.entity.ResponseFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ResponseFormationRepository extends JpaRepository<ResponseFormation, Long> {

    @Query("SELECT r FROM ResponseFormation r LEFT JOIN FETCH r.blockAnswers WHERE r.evaluation.id = :evaluationId")
    List<ResponseFormation> findByEvaluationId(Long evaluationId);


    List<ResponseFormation> findByEvaluationIdAndUserUserId(Long evaluationId, Long userId);
}
