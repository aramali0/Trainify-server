package e_learning.DTO;

import lombok.Builder;

import java.util.List;

@Builder
public record ResponseDto(
        Long id,
        List<Long> optionId,
        Long evaluationId,
        Long questionId
) {}
