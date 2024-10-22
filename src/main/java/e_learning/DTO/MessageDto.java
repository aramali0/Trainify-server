package e_learning.DTO;

import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record MessageDto(
        String content,
        Date timestamp,
        boolean isRead,
        Long senderId,
        Long receiverId
) {}