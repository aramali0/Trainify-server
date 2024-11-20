package e_learning.DTO;

import java.util.List;

public record EvaluationFormationDto(
    Long id,
    String title,
    String type,
    List<String> questions,
    Long createdBy,
    Long entrepriseId,
    Long createdAt
) {
}
