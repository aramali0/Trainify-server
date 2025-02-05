package e_learning.DTO;

public record ResourceDto(
        Long id,
        String title,
        String type,
        String filePath,
        boolean isDownloadable,
        boolean isApproved,
        Long libraryId,
        Long sectionId,
        Long createdBy
) {}