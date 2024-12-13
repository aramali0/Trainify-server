package e_learning.repositories;

import e_learning.entity.EvaluationBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationBlockRepository extends JpaRepository<EvaluationBlock, Long> {
}
