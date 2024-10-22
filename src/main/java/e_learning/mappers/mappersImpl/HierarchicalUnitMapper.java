package e_learning.mappers.mappersImpl;

import e_learning.DTO.HierarchicalUnitDto;
import e_learning.entity.HierarchicalUnit;
import e_learning.repositories.EntrepriseRepository;
import e_learning.repositories.HierarchicalUnitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
public class HierarchicalUnitMapper {

    private final HierarchicalUnitRepository hierarchicalUnitRepository;
    private final EntrepriseRepository entrepriseRepository;


    public HierarchicalUnitDto toDto(HierarchicalUnit unit) {
        HierarchicalUnitDto dto = new HierarchicalUnitDto();
        if(unit.getId() != null) dto.setId(unit.getId());
        dto.setName(unit.getName());
        dto.setType(unit.getType());
        if(unit.getParent() != null) {
            dto.setParentId(unit.getParent().getId());
        }
        if (unit.getEntreprise() != null) {
            dto.setEntrepriseId(unit.getEntreprise().getId());
        }

        if (unit.getChildren() != null) {
            dto.setChildrenIds(unit.getChildren().stream().map(HierarchicalUnit::getId).collect(Collectors.toList()));
        }

        return dto;
    }

    public HierarchicalUnit toEntity(HierarchicalUnitDto dto) {
        HierarchicalUnit unit = new HierarchicalUnit();
        if(dto.getId() != null) unit.setId(dto.getId());
        unit.setName(dto.getName());
        unit.setType(dto.getType());

        if (dto.getParentId() != null) {
            unit.setParent(hierarchicalUnitRepository.findById(dto.getParentId()).orElseThrow(() -> new RuntimeException("Parent not found "))); // Set the parent reference{
        }

        if (dto.getEntrepriseId() != null) {
            unit.setEntreprise(entrepriseRepository.findById(dto.getEntrepriseId())
                    .orElseThrow(() -> new RuntimeException("Entreprise not found")));
        }


        if (dto.getChildrenIds() != null) {
            unit.setChildren(dto.getChildrenIds().stream().map(id -> hierarchicalUnitRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Child unit not found"))).collect(Collectors.toList()));
        }


        return unit;
    }
}