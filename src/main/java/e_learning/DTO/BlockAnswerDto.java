package e_learning.DTO;

import java.util.Map;

public record BlockAnswerDto(
        Long blockId,
        double totalScore,
        Map<String, Integer> answers
) {}
