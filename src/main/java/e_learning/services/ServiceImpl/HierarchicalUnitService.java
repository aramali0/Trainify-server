package e_learning.services.ServiceImpl;

import e_learning.DTO.HierarchicalUnitDto;
import e_learning.entity.Entreprise;
import e_learning.entity.HierarchicalUnit;
import e_learning.mappers.mappersImpl.HierarchicalUnitMapper;
import e_learning.repositories.EntrepriseRepository;
import e_learning.repositories.HierarchicalUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HierarchicalUnitService {

    private final HierarchicalUnitRepository hierarchicalUnitRepository;
    private final HierarchicalUnitMapper hierarchicalUnitMapper;
    private final EntrepriseRepository entrepriseRepository;



    public HierarchicalUnitDto createOrUpdateUnit(HierarchicalUnitDto dto) {
        // Ensure the unit is associated with an enterprise
        HierarchicalUnit unit = hierarchicalUnitMapper.toEntity(dto);

        unit = hierarchicalUnitRepository.save(unit);

        return hierarchicalUnitMapper.toDto(unit);
    }

    // Get all hierarchical units of a specific entreprise
    public List<HierarchicalUnitDto> getUnitsByEntreprise(Long entrepriseId) {
        List<HierarchicalUnit> units = hierarchicalUnitRepository.findByEntrepriseId(entrepriseId);
        return units.stream().map(hierarchicalUnitMapper::toDto).collect(Collectors.toList());
    }

    // Get a specific hierarchical unit by ID
    public HierarchicalUnitDto getUnitById(String id) {
        HierarchicalUnit unit = hierarchicalUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        return hierarchicalUnitMapper.toDto(unit);
    }

    // Delete a hierarchical unit
    public void deleteUnit(String id) {
        hierarchicalUnitRepository.deleteById(id);
    }

    public List<HierarchicalUnitDto> getLeafsByEntreprise(String entrepriseNomCommercial) {
        List<HierarchicalUnit> units = hierarchicalUnitRepository.findByEntrepriseNomCommercial(entrepriseNomCommercial);
        return units.stream().filter(unit -> unit.getChildren().isEmpty()).map(hierarchicalUnitMapper::toDto).collect(Collectors.toList());
    }
}
