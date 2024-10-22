package e_learning.services.ServiceImpl;


import e_learning.entity.PricingPlan;
import e_learning.repositories.PricingPlanRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class PricingPlanService {

    private final PricingPlanRepository repository;

    public List<PricingPlan> getAllPricingPlans() {
        return repository.findAll();
    }

    public Optional<PricingPlan> getPricingPlanById(Long id) {
        return repository.findById(id);
    }

    public PricingPlan createPricingPlan(PricingPlan plan) {
        return repository.save(plan);
    }

    public PricingPlan updatePricingPlan(Long id, PricingPlan updatedPlan) {
        return repository.findById(id).map(plan -> {
            plan.setPlanAR(updatedPlan.getPlanAR());
            plan.setPlanFR(updatedPlan.getPlanFR());
            plan.setPlanEN(updatedPlan.getPlanEN());
            plan.setPrice(updatedPlan.getPrice());
            plan.setFeaturesAR(updatedPlan.getFeaturesAR());
            plan.setFeaturesFR(updatedPlan.getFeaturesFR());
            plan.setFeaturesEN(updatedPlan.getFeaturesEN());
            plan.setPopular(updatedPlan.isPopular());
            plan.setPer(updatedPlan.getPer());
            plan.setLink(updatedPlan.getLink());
            return repository.save(plan);

        }).orElseThrow(() -> new RuntimeException("Plan not found with id " + id));
    }

    public void deletePricingPlan(Long id) {
        repository.deleteById(id);
    }
}
