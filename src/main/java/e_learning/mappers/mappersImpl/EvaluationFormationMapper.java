package e_learning.mappers.mappersImpl;

import e_learning.DTO.EvaluationFormationDto;
import e_learning.entity.EvaluationFormation;
import e_learning.enums.EvaluationType;
import e_learning.repositories.EvaluationFormationRepository;
import e_learning.repositories.ResponseFormationRepository;
import e_learning.repositories.UserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EvaluationFormationMapper {

    private final ResponseFormationRepository responseFormationRepository;
    private final EvaluationFormationRepository evaluationFormationRepository;
    private final UserAppRepository userAppRepository;

    public EvaluationFormationDto toDto(EvaluationFormation entity) {
        return new EvaluationFormationDto(
                entity.getId() != null ? entity.getId() : null,
                entity.getTitle(),
                entity.getType().name(),
                entity.getQuestions(),
                entity.getCreatedBy() != null ? entity.getCreatedBy().getUserId() : null,
                entity.getEntreprise() != null ? entity.getEntreprise().getId() : null,
                entity.getCreatedAt().getTime()
        );
    }

    public EvaluationFormation toEntity(EvaluationFormationDto dto) {
        EvaluationFormation evaluation = new EvaluationFormation();
        evaluation.setTitle(dto.title());
        evaluation.setType(EvaluationType.valueOf(dto.type().toUpperCase()));
        evaluation.setQuestions(dto.questions());
        evaluation.setCreatedBy(userAppRepository.findById(dto.createdBy())
                .orElseThrow(() -> new RuntimeException("User not found")));
        evaluation.setCreatedAt(new Date(dto.createdAt()));
        return evaluationFormationRepository.save(evaluation);
    }

    public List<EvaluationFormation> toEntities(List<EvaluationFormationDto> dtos) {
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public List<EvaluationFormationDto> toDtos(List<EvaluationFormation> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
