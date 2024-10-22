package e_learning.DTO;

import e_learning.enums.Gender;
import e_learning.enums.Langue;
import io.micrometer.common.lang.Nullable;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record CourDto(
        Long id,
        String titre,
        String subTitre,
        String description,
        Date createdAt,
        Date miseAJour,
        String langue,
        int duree,
        boolean isApproved,
        boolean isSent,
        Long entrepriseId,
        List<Long> formateurIds,
        List<Long> classIds,
        List<Long> sessionIds,
        @Nullable String imagePath,
        Boolean isFavorite
) {}
