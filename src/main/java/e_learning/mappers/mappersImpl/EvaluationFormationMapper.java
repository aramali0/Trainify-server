package e_learning.mappers.mappersImpl;

import e_learning.DTO.EvaluationFormationDto;
import e_learning.entity.EvaluationFormation;
import e_learning.entity.UserApp;
import e_learning.enums.EvaluationType;
import e_learning.repositories.EvaluationFormationRepository;
import e_learning.repositories.ResponseFormationRepository;
import e_learning.repositories.UserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EvaluationFormationMapper {
    private final EvaluationBlockMapper evaluationBlockMapper;

    public EvaluationFormationDto toDto(EvaluationFormation entity) {
        return new EvaluationFormationDto(
                entity.getId(),
                entity.getTitle(),
                entity.getType().name(),
                entity.getBlocks().stream().map(evaluationBlockMapper::toDto).toList(),
                entity.getCreatedBy() != null ? entity.getCreatedBy().getUserId() : null,
                entity.getCour() != null ? entity.getCour().getId() : null,
                entity.getCreatedAt().getTime()
        );
    }

    public EvaluationFormation toEntity(EvaluationFormationDto dto) {
        EvaluationFormation evaluation = new EvaluationFormation();
        evaluation.setTitle(dto.title());
        evaluation.setType(EvaluationType.valueOf(dto.type().toUpperCase()));
        evaluation.setBlocks(dto.blocks().stream()
                .map(evaluationBlockMapper::toEntity)
                .collect(Collectors.toCollection(ArrayList::new)));
        if(dto.createdAt() != null)
        {
            evaluation.setCreatedAt(new Date(dto.createdAt()));
        }
        else{
            evaluation.setCreatedAt(new Date());
        }

        return evaluation;
    }
}
