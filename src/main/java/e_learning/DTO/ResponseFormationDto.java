package e_learning.DTO;

import java.util.Map;

public record ResponseFormationDto(
        Long id,
        Long evaluationId,
        Long userId,
        Long entrepriseId,
        Map<String, Integer> answers,
        Double percentage
) {
}
