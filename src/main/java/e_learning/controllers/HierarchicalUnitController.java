package e_learning.controllers;

import e_learning.DTO.HierarchicalUnitDto;
import e_learning.services.ServiceImpl.HierarchicalUnitService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hierarchical-units")
@AllArgsConstructor
public class HierarchicalUnitController {

    private final HierarchicalUnitService hierarchicalUnitService;


    // Create or update a hierarchical unit
    @PostMapping
    public ResponseEntity<HierarchicalUnitDto> createOrUpdateUnit( @RequestBody HierarchicalUnitDto dto) {
        HierarchicalUnitDto savedDto = hierarchicalUnitService.createOrUpdateUnit(dto);
        return ResponseEntity.ok(savedDto);
    }

    // Get all hierarchical units of an entreprise
    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<HierarchicalUnitDto>> getUnitsByEntreprise(@PathVariable Long entrepriseId) {
        List<HierarchicalUnitDto> dtos = hierarchicalUnitService.getUnitsByEntreprise(entrepriseId);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/entreprise/{entrepriseId}/leafs")
    public ResponseEntity<List<HierarchicalUnitDto>> getLeafsByEntreprise(@PathVariable String entrepriseId) {
        System.out.println("entrepriseId = " + entrepriseId);
        List<HierarchicalUnitDto> dtos = hierarchicalUnitService.getLeafsByEntreprise(entrepriseId);
        System.out.println("dtos from controller  = " + dtos);
        return ResponseEntity.ok(dtos);
    }

    // Get a specific hierarchical unit by ID
    @GetMapping("/{id}")
    public ResponseEntity<HierarchicalUnitDto> getUnitById(@PathVariable String id) {
        HierarchicalUnitDto dto = hierarchicalUnitService.getUnitById(id);
        return ResponseEntity.ok(dto);
    }

    // Delete a hierarchical unit
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUnit(@PathVariable String id) {
        hierarchicalUnitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
