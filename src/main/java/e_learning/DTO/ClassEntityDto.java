package e_learning.DTO;

import e_learning.enums.Langue;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record ClassEntityDto(
        Long id,
        String titre,
        boolean isApproved,
        Long createdBy,
        List<Long> participantIds,
        List<Long> courIds
) {}