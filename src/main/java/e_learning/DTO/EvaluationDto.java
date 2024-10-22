package e_learning.DTO;

import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record EvaluationDto(
        Long id,
        String type,
        Long score,
        Date createdAt,
        Long timeTaken,
        List<Long> responseIds,
        Long participantId,
        Long quizId

) {}