package e_learning.DTO;

import lombok.Builder;
import java.util.Date;

@Builder
public record VideoConferenceDto(
        Long id,
        String title,
        String platform,
        String url,
        Date startTime,
        Long sessionId
) {}
