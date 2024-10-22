package e_learning.controllers;

import e_learning.entity.PricingPlan;
import e_learning.services.ServiceImpl.PricingPlanService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/pricing")
@AllArgsConstructor
public class PricingPlanController {

    private final PricingPlanService service;

    @GetMapping
    public List<PricingPlan> getAllPricingPlans() {
        return service.getAllPricingPlans();
    }

    @GetMapping("/{id}")
    public PricingPlan getPricingPlanById(@PathVariable Long id) {
        return service.getPricingPlanById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id " + id));
    }

    @PostMapping
    public PricingPlan createPricingPlan(@RequestBody PricingPlan plan) {
        return service.createPricingPlan(plan);
    }

    @PutMapping("/{id}")
    public PricingPlan updatePricingPlan(@PathVariable Long id, @RequestBody PricingPlan updatedPlan) {
        return service.updatePricingPlan(id, updatedPlan);
    }

    @DeleteMapping("/{id}")
    public void deletePricingPlan(@PathVariable Long id) {
        service.deletePricingPlan(id);
    }
}
