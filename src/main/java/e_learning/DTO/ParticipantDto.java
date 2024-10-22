package e_learning.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import e_learning.enums.Gender;
import e_learning.enums.Langue;
import e_learning.enums.UserRole;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record ParticipantDto(
        Long id,
        String imagePath,
        String firstName,
        String lastName,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String num,
        String cin,
        Gender gender,
        int age,
        Date createdAt,
        List<Long> classIds,
        List<Long> unavailabilities,
        String hierarchicalUnitId
) {}
