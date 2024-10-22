package e_learning.repositories;

import e_learning.entity.ActionApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionApprovalRepository extends JpaRepository<ActionApproval, Long> {

    List<ActionApproval> findByEntrepriseIdAndApprovedFalseAndRejectedFalseAndUpdateRequestedFalse(Long EntrepriseId);

    List<ActionApproval> findByChargeFormationId(Long id);
}
