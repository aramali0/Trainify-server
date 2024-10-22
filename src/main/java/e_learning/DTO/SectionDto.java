package e_learning.DTO;

import lombok.Builder;

import java.util.Date;
import java.util.List;
@Builder
public record SectionDto(
        Long id,
        String title,
        String content,
        Date createdAt,
        Date startDate,
        Date endDate,
        boolean isApproved,
        boolean isSent,
        Long sessionId,
        Long createdById,
        List<Long> resourceIds,
        List<Long> quizIds
) {}