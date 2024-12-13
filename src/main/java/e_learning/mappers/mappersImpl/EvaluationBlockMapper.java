package e_learning.mappers.mappersImpl;

import e_learning.DTO.EvaluationBlockDto;
import e_learning.entity.EvaluationBlock;
import org.springframework.stereotype.Component;

@Component
public class EvaluationBlockMapper {
    public EvaluationBlockDto toDto(EvaluationBlock entity) {
        return new EvaluationBlockDto(
                entity.getId(),
                entity.getTitle(),
                entity.getWeightage(),
                entity.getQuestions()
        );
    }

    public EvaluationBlock toEntity(EvaluationBlockDto dto) {
        EvaluationBlock block = new EvaluationBlock();
        block.setTitle(dto.title());
        block.setWeightage(dto.weightage());
        block.setQuestions(dto.questions());
        return block;
    }
}
