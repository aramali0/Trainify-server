package e_learning.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import e_learning.entity.RoleApp;
import e_learning.enums.Gender;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Builder
public record UserDTO(
        Long userId,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        int age,
        String firstName,
        String lastName,
        String CIN,
        String num,
        Gender gender,
        boolean isVerified,
        boolean isEnabled,
        LocalDateTime lastActiveTime,
        String profileImagePath, // New field
        Date createdAt,
        List<RoleApp> roles
) {
}
