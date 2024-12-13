package e_learning.DTO;

import java.util.List;
import java.util.Map;

public record ResponseFormationDto(
        Long id,
        Long evaluationId,
        Long userId,
        List<BlockAnswerDto> blockAnswers,
        Double totalScore
) {
}
