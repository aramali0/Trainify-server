package e_learning.DTO;

import java.util.List;

public record LibraryDto(
        Long id,
        String name,
        Long courId,
        Long createdBy,
        boolean isApproved,
        List<Long> resourceIds
) {}