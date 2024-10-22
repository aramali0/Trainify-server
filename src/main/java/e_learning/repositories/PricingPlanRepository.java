package e_learning.repositories;

import e_learning.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingPlanRepository extends JpaRepository<PricingPlan, Long> {
}
