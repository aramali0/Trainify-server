package e_learning.DTO;

import java.time.LocalDateTime;


    public record ContactDto(Long userId,
                             String firstName,
                             String lastName,
                             LocalDateTime lastActiveTime,
                             int unreadMessageCount) {}

