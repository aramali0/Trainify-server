package e_learning.DTO;

import java.util.List;

public record QuizDto(
        Long id,
        String title,
        String type,
        Long createdBy,
        Long sectionId,
        List<Long> questionIds,
        List<Long> evaluationsIds
) {}
