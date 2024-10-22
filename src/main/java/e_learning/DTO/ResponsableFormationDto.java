package e_learning.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import e_learning.enums.Gender;
import e_learning.enums.Langue;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record ResponsableFormationDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String num,
        Gender gender,
        int age,
        Date createdAt,
        Long entrepriseId
) {}