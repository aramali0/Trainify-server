package e_learning.DTO;

import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record SessionDto(
        Long id,
        String name,
        Date sessionDate,
        int duree,
        Date startDate,
        Date endDate,
        boolean isApproved,
        boolean isSent,
        Long courId,
        Long createdById,
       List<Long> videoConferenceIds,
        List<Long> sectionIds
) {}