package e_learning.DTO;

import java.util.List;

public record EvaluationBlockDto(
        Long id,
        String title,
        Double weightage,
        List<String> questions
) {
}
