package e_learning.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record QuestionDto(
        Long id,
        String text,
        String type,
        List<Long> responseIds,
        List<Long> optionIds,
        Long quizId
) {}