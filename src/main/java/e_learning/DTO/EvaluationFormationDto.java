package e_learning.DTO;

import java.util.List;

public record EvaluationFormationDto(
        Long id,
        String title,
        String type,
        List<EvaluationBlockDto> blocks,
        Long createdBy,
        Long coursId,
        Long createdAt
) {
}
