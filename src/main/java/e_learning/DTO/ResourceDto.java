package e_learning.DTO;

public record ResourceDto(
        Long id,
        String title,
        String type,
        String filePath,
        boolean isApproved,
        Long libraryId,
        Long sectionId,
        Long createdBy
) {}